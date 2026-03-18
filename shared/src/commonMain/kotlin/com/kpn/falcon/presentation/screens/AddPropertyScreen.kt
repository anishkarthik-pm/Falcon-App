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
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Add Property Wizard screen (7 steps).
 * Stub — full implementation in Task 5.
 * Screen class (not object) so it can carry nav parameters in future.
 */
class AddPropertyScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AddPropertyViewModel>()
        val state by viewModel.uiState.collectAsState()

        KPNTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
                Column(modifier = Modifier.fillMaxSize()) {
                    WizardHeader(
                        currentStep = state.currentStep,
                        totalSteps = state.totalSteps,
                        onClose = { navigator.pop() }
                    )
                    WizardTabRow(
                        tabs = listOf("Location", "Store Specs", "Road & Access", "Media", "Commercials", "Competition", "Contact"),
                        currentStep = state.currentStep,
                        onTabSelected = { viewModel.goToStep(it) }
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Step ${state.currentStep} of ${state.totalSteps}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = KPNColors.TextPrimary
                            )
                            Text(
                                text = "Full wizard UI coming in Task 5",
                                style = MaterialTheme.typography.bodyLarge,
                                color = KPNColors.TextSecondary
                            )
                            WizardNavRow(
                                onBack = if (state.currentStep > 1) ({ viewModel.previousStep() }) else null,
                                onNext = {
                                    if (state.currentStep < state.totalSteps) viewModel.nextStep()
                                    else navigator.pop()
                                },
                                nextLabel = if (state.currentStep == state.totalSteps) "Submit" else "Next"
                            )
                        }
                    }
                }
            }
        }
    }
}
