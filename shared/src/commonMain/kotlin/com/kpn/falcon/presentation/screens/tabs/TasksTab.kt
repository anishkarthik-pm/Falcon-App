package com.kpn.falcon.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.kpn.falcon.util.Strings

object TasksTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Notifications)
            return remember { TabOptions(index = 3u, title = Strings.TAB_TASKS, icon = icon) }
        }

    @Composable
    override fun Content() {
        NotificationsScreen()
    }
}
