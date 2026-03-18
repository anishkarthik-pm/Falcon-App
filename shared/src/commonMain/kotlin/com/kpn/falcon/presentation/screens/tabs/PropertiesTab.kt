package com.kpn.falcon.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.kpn.falcon.util.Strings

/** Root screen for the Properties tab's nested navigator. */
object PropertiesListScreen : Screen {
    @Composable
    override fun Content() = PropertiesScreen()
}

object PropertiesTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Business)
            return remember { TabOptions(index = 1u, title = Strings.TAB_PROPERTIES, icon = icon) }
        }

    @Composable
    override fun Content() {
        // Nested navigator: Properties list → Detail / Add screens,
        // without affecting the root bottom-nav navigator.
        Navigator(screen = PropertiesListScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
