package de.k7labor.fahrwerk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.k7labor.fahrwerk.ui.DataScreen
import de.k7labor.fahrwerk.ui.K7Theme
import de.k7labor.fahrwerk.ui.LibraryScreen
import de.k7labor.fahrwerk.ui.RigScreen
import de.k7labor.fahrwerk.ui.SetupScreen
import de.k7labor.fahrwerk.ui.WorkshopScreen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val vm: LabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { K7Theme { App(vm) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(vm: LabViewModel) {
    val tab by vm.tab.collectAsState()
    val snack = remember { SnackbarHostState() }
    // collectLatest bricht die laufende Meldung ab (blendet sie aus), statt neue Meldungen hinten anzustellen
    LaunchedEffect(Unit) { vm.messages.collectLatest { snack.showSnackbar(it) } }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("K7 Fahrwerkslabor · ${tab.title}") }) },
        bottomBar = {
            NavigationBar {
                for (t in Tab.entries) NavigationBarItem(
                    selected = tab == t,
                    onClick = { vm.tab.value = t },
                    icon = { Text(t.glyph, modifier = Modifier.semantics { contentDescription = "" }) },
                    label = { Text(t.title) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snack) }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                Tab.SETUP -> SetupScreen(vm)
                Tab.RIG -> RigScreen(vm)
                Tab.WORKSHOP -> WorkshopScreen(vm)
                Tab.LIBRARY -> LibraryScreen(vm)
                Tab.DATA -> DataScreen(vm)
            }
        }
    }
}
