package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.*
import com.kpn.falcon.di.SessionManager
import com.kpn.falcon.domain.entities.UserRole
import com.kpn.falcon.presentation.components.OfflineBanner
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.util.NetworkMonitor
import org.koin.compose.koinInject

object MainScreen : Screen {

    @Composable
    override fun Content() {
        val networkMonitor = koinInject<NetworkMonitor>()
        val sessionManager = koinInject<SessionManager>()
        val isOnline by networkMonitor.isConnected.collectAsState()
        val currentUser by sessionManager.currentUser.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        TabNavigator(tab = HomeTab) { tabNavigator ->
            Scaffold(
                bottomBar = {
                    KPNBottomNav(tabNavigator)
                },
                floatingActionButton = {
                    // FAB only for BD_EXECUTIVE — Add Property shortcut
                    if (currentUser?.role == UserRole.BD_EXECUTIVE &&
                        tabNavigator.current == PropertiesTab
                    ) {
                        FloatingActionButton(
                            onClick = { navigator.push(AddPropertyScreen()) },
                            containerColor = KPNColors.AccentOrange,
                            contentColor = KPNColors.Surface,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Property")
                        }
                    }
                }
            ) { padding ->
                Column(modifier = Modifier.padding(padding)) {
                    if (!isOnline) {
                        OfflineBanner()
                    }
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun KPNBottomNav(tabNavigator: TabNavigator) {
    NavigationBar(
        containerColor = KPNColors.Surface,
        tonalElevation = 4.dp
    ) {
        listOf(HomeTab, PropertiesTab, PipelineTab, TasksTab).forEach { tab ->
            val isSelected = tabNavigator.current == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { tabNavigator.current = tab },
                icon = {
                    if (isSelected) {
                        ActiveTabPill(icon = tab.options.icon!!, label = tab.options.title)
                    } else {
                        Icon(
                            painter = tab.options.icon!!,
                            contentDescription = tab.options.title,
                            tint = KPNColors.TextSecondary
                        )
                    }
                },
                label = null,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = KPNColors.Surface
                )
            )
        }
    }
}

@Composable
private fun ActiveTabPill(icon: Painter, label: String) {
    Row(
        modifier = Modifier
            .background(KPNColors.Primary, shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = KPNColors.PrimaryText,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = KPNColors.PrimaryText
        )
    }
}
