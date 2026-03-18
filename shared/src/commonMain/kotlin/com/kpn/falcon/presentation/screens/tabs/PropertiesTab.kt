package com.kpn.falcon.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.kpn.falcon.util.Strings

object PropertiesTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Business)
            return remember { TabOptions(index = 1u, title = Strings.TAB_PROPERTIES, icon = icon) }
        }

    @Composable
    override fun Content() {
        PropertiesScreen()
    }
}
