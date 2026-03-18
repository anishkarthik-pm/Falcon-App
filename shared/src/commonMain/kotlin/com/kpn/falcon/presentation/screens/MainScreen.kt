package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.util.Strings

object MainScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(tab = HomeTab) { tabNavigator ->
            Scaffold(
                bottomBar = {
                    KPNBottomNav(tabNavigator)
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
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
                        ActiveTabPill(tab.options.icon!!, tab.options.title)
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
private fun ActiveTabPill(
    icon: androidx.compose.ui.graphics.painter.Painter,
    label: String
) {
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
