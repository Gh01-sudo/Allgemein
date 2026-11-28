package de.k7labor.fahrwerk.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.k7labor.fahrwerk.physics.fmt
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class Series(
    val label: String,
    val x: DoubleArray,
    val y: DoubleArray,
    val color: Color,
    val dashed: Boolean = false,
    val width: Float = 2.2f
)

fun FloatArray.toD(n: Int = size) = DoubleArray(n) { this[it].toDouble() }

private fun niceStep(range: Double, target: Int): Double {
    if (range <= 0 || !range.isFinite()) return 1.0
    val raw = range / target
    val mag = 10.0.pow(floor(log10(raw)))
    val f = raw / mag
    val nice = when { f < 1.5 -> 1.0; f < 3.5 -> 2.0; f < 7.5 -> 5.0; else -> 10.0 }
    return nice * mag
}

/**
 * Einfaches Liniendiagramm mit Achsen, Gitter und Legende. Optional logarithmische x-Achse und
 * ein senkrechter Zeit-Cursor (für die Animation).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LineChart(
    series: List<Series>,
    xLabel: String,
    yLabel: String,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    logX: Boolean = false,
    yMin: Double? = null,
    yMax: Double? = null,
    xMin: Double? = null,
    xMax: Double? = null,
    cursorX: Double? = null,
    hLines: List<Pair<Double, String>> = emptyList(),
    xDecimals: Int = 1,
    yDecimals: Int = 0
) {
    val measurer = rememberTextMeasurer()
    val onSurf = MaterialTheme.colorScheme.onSurfaceVariant
    val lab = LocalLabColors.current
    val desc = "Diagramm $yLabel über $xLabel: " + series.joinToString("; ") { s ->
        val ymax = s.y.maxOrNull() ?: 0.0
        "${s.label} bis ${fmt(ymax, yDecimals)}"
    }
    Column(modifier) {
        Box(Modifier.fillMaxWidth().height(height).semantics { contentDescription = desc }) {
            Canvas(Modifier.fillMaxWidth().height(height)) {
                val padL = 46.dp.toPx(); val padR = 8.dp.toPx(); val padT = 8.dp.toPx(); val padB = 30.dp.toPx()
                val w = size.width - padL - padR
                val h = size.height - padT - padB
                if (w <= 0 || h <= 0) return@Canvas
                val all = series.filter { it.x.isNotEmpty() }
                fun tx(v: Double) = if (logX) log10(max(v, 1e-6)) else v
                var x0 = xMin ?: all.minOfOrNull { s -> s.x.min() } ?: 0.0
                var x1 = xMax ?: all.maxOfOrNull { s -> s.x.max() } ?: 1.0
                var y0 = yMin ?: all.minOfOrNull { s -> s.y.filter { it.isFinite() }.minOrNull() ?: 0.0 } ?: 0.0
                var y1 = yMax ?: all.maxOfOrNull { s -> s.y.filter { it.isFinite() }.maxOrNull() ?: 1.0 } ?: 1.0
                for ((v, _) in hLines) { y0 = min(y0, v); y1 = max(y1, v) }
                if (x1 <= x0) x1 = x0 + 1
                if (y1 - y0 < 1e-9) { y1 += 1; y0 -= 1 }
                if (yMin == null || yMax == null) {
                    val pad = (y1 - y0) * 0.06
                    if (yMin == null) y0 -= pad
                    if (yMax == null) y1 += pad
                }
                val lx0 = tx(x0); val lx1 = tx(x1)
                fun px(v: Double) = padL + ((tx(v) - lx0) / (lx1 - lx0) * w).toFloat()
                fun py(v: Double) = padT + (h - (v - y0) / (y1 - y0) * h).toFloat()
                val style = TextStyle(fontSize = 10.sp, color = onSurf)

                // y-Gitter
                val ys = niceStep(y1 - y0, 4)
                var yv = ceil(y0 / ys) * ys
                while (yv <= y1 + 1e-9) {
                    val p = py(yv)
                    drawLine(lab.grid, Offset(padL, p), Offset(padL + w, p), 1f)
                    val txt = measurer.measure(fmt(yv, if (ys < 1) max(yDecimals, if (ys < 0.1) 2 else 1) else yDecimals), style)
                    drawText(txt, topLeft = Offset(padL - txt.size.width - 4.dp.toPx(), p - txt.size.height / 2))
                    yv += ys
                }
                // x-Gitter
                if (logX) {
                    val decades = listOf(0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0)
                    for (d in decades) if (d >= x0 - 1e-9 && d <= x1 + 1e-9) {
                        val p = px(d)
                        drawLine(lab.grid, Offset(p, padT), Offset(p, padT + h), 1f)
                        val txt = measurer.measure(fmt(d, if (d < 1) 1 else 0), style)
                        drawText(txt, topLeft = Offset(p - txt.size.width / 2, padT + h + 3.dp.toPx()))
                    }
                } else {
                    val xs = niceStep(x1 - x0, 5)
                    var xv = ceil(x0 / xs) * xs
                    while (xv <= x1 + 1e-9) {
                        val p = px(xv)
                        drawLine(lab.grid, Offset(p, padT), Offset(p, padT + h), 1f)
                        val txt = measurer.measure(fmt(xv, if (xs < 1) xDecimals else 0), style)
                        drawText(txt, topLeft = Offset(p - txt.size.width / 2, padT + h + 3.dp.toPx()))
                        xv += xs
                    }
                }
                if (y0 < 0 && y1 > 0) drawLine(onSurf.copy(alpha = 0.5f), Offset(padL, py(0.0)), Offset(padL + w, py(0.0)), 1.2f)
                for ((v, label) in hLines) {
                    val p = py(v)
                    drawLine(lab.bad.copy(alpha = 0.7f), Offset(padL, p), Offset(padL + w, p), 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                    val txt = measurer.measure(label, style.copy(color = lab.bad))
                    drawText(txt, topLeft = Offset(padL + w - txt.size.width - 2, p - txt.size.height - 1))
                }
                // Achsen
                drawLine(onSurf, Offset(padL, padT), Offset(padL, padT + h), 1.5f)
                drawLine(onSurf, Offset(padL, padT + h), Offset(padL + w, padT + h), 1.5f)
                // Kurven
                clipRect(left = padL, top = padT, right = padL + w, bottom = padT + h) {
                    for (s in all) {
                        val path = Path()
                        var started = false
                        val n = min(s.x.size, s.y.size)
                        val stride = max(1, n / 800)
                        var i = 0
                        while (i < n) {
                            val yy = s.y[i]
                            if (yy.isFinite()) {
                                val X = px(s.x[i]); val Y = py(yy)
                                if (!started) { path.moveTo(X, Y); started = true } else path.lineTo(X, Y)
                            }
                            i += stride
                        }
                        drawPath(
                            path, s.color,
                            style = Stroke(width = s.width.dp.toPx() / 1.5f, cap = StrokeCap.Round, pathEffect = if (s.dashed) PathEffect.dashPathEffect(floatArrayOf(10f, 8f)) else null)
                        )
                    }
                    cursorX?.let { cx ->
                        val p = px(cx)
                        drawLine(onSurf.copy(alpha = 0.8f), Offset(p, padT), Offset(p, padT + h), 1.5f)
                    }
                }
                // Achsbeschriftung
                val yl = measurer.measure(yLabel, style)
                drawText(yl, topLeft = Offset(padL + 4.dp.toPx(), padT))
                val xl = measurer.measure(xLabel, style)
                drawText(xl, topLeft = Offset(padL + w - xl.size.width, padT + h + 14.dp.toPx()))
            }
        }
        FlowRow(Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for (s in series) Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.width(18.dp).height(10.dp)) {
                    drawLine(s.color, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 3f,
                        pathEffect = if (s.dashed) PathEffect.dashPathEffect(floatArrayOf(6f, 4f)) else null)
                }
                Text(" " + s.label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

fun absMax(a: DoubleArray) = a.maxOfOrNull { abs(it) } ?: 0.0
