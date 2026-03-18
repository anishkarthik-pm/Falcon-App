package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.presentation.viewmodels.PropertyDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Property Detail screen with 5 tabs.
 * Stub — full implementation in Task 6.
 */
data class PropertyDetailScreen(val propertyId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<PropertyDetailViewModel> { parametersOf(propertyId) }
        val state by viewModel.uiState.collectAsState()

        KPNTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
                when {
                    state.isLoading -> KPNLoadingScreen()
                    state.error != null -> KPNErrorScreen(
                        message = state.error!!,
                        onRetry = viewModel::loadProperty
                    )
                    state.property != null -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            KPNTopBar(
                                title = state.property!!.propertyId,
                                onNavigateBack = { navigator.pop() }
                            )
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Property Detail — full UI in Task 6",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = KPNColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
