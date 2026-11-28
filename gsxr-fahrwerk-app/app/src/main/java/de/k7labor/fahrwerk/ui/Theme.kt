package de.k7labor.fahrwerk.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** Feste Kurvenfarben, in hell und dunkel gut unterscheidbar. */
@Immutable
data class LabColors(
    val front: Color, val rear: Color, val stock: Color, val good: Color, val warn: Color, val bad: Color,
    val grid: Color, val road: Color, val extra: Color
)

val LocalLabColors = staticCompositionLocalOf {
    LabColors(Color(0xFF1565C0), Color(0xFFD84315), Color(0xFF7A7A7A), Color(0xFF2E7D32), Color(0xFFB26A00), Color(0xFFC62828), Color(0x22000000), Color(0xFF5D4037), Color(0xFF6A1B9A))
}

private val lightLab = LabColors(
    front = Color(0xFF1565C0), rear = Color(0xFFD84315), stock = Color(0xFF8A8A8A),
    good = Color(0xFF2E7D32), warn = Color(0xFFA15C00), bad = Color(0xFFC62828),
    grid = Color(0x1F000000), road = Color(0xFF6D4C41), extra = Color(0xFF6A1B9A)
)
private val darkLab = LabColors(
    front = Color(0xFF64B5F6), rear = Color(0xFFFF8A65), stock = Color(0xFF9E9E9E),
    good = Color(0xFF81C784), warn = Color(0xFFFFB74D), bad = Color(0xFFEF9A9A),
    grid = Color(0x26FFFFFF), road = Color(0xFFBCAAA4), extra = Color(0xFFCE93D8)
)

@Composable
fun K7Theme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val ctx = LocalContext.current
    // Ab Android 12 (Mindestversion der App) gibt es immer dynamische Farben vom Hintergrundbild
    val scheme: ColorScheme = if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
    androidx.compose.runtime.CompositionLocalProvider(LocalLabColors provides if (dark) darkLab else lightLab) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
