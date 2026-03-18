package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kpn.falcon.di.SessionManager
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.screens.wizard.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.Strings
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private val WIZARD_TABS = listOf(
    Strings.WIZARD_STEP_LOCATION,
    Strings.WIZARD_STEP_STORE_SPECS,
    Strings.WIZARD_STEP_ROAD,
    Strings.WIZARD_STEP_MEDIA,
    Strings.WIZARD_STEP_COMMERCIALS,
    Strings.WIZARD_STEP_COMPETITION,
    Strings.WIZARD_STEP_CONTACT
)

class AddPropertyScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AddPropertyViewModel>()
        val sessionManager = koinInject<SessionManager>()
        val state by viewModel.uiState.collectAsState()
        val currentUser by sessionManager.currentUser.collectAsState()

        // Show close confirmation if user navigates away mid-wizard
        var showCloseDialog by remember { mutableStateOf(false) }

        // Success screen
        if (state.isSubmitted) {
            KPNSuccessScreen(
                title = Strings.SUBMIT_SUCCESS,
                onDone = { navigator.pop() }
            )
            return
        }

        KPNTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // ── Wizard header ──────────────────────────
                    WizardHeader(
                        currentStep = state.currentStep,
                        totalSteps = state.totalSteps,
                        onClose = { showCloseDialog = true }
                    )

                    // ── Tab row ───────────────────────────────
                    WizardTabRow(
                        tabs = WIZARD_TABS,
                        currentStep = state.currentStep,
                        onTabSelected = { viewModel.goToStep(it) }
                    )

                    // ── Step content ──────────────────────────
                    Box(modifier = Modifier.weight(1f)) {
                        when (state.currentStep) {
                            1 -> Step1LocationContent(viewModel = viewModel, state = state)
                            2 -> Step2StoreSpecsContent(viewModel = viewModel, state = state)
                            3 -> Step3RoadAccessContent(viewModel = viewModel, state = state)
                            4 -> Step4MediaContent(viewModel = viewModel, state = state)
                            5 -> Step5CommercialsContent(viewModel = viewModel, state = state)
                            6 -> Step6CompetitionContent(viewModel = viewModel, state = state)
                            7 -> Step7ContactContent(viewModel = viewModel, state = state)
                        }
                    }

                    // ── Error snackbar ────────────────────────
                    if (state.error != null) {
                        Snackbar(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            containerColor = KPNColors.AccentRed,
                            contentColor = KPNColors.Surface
                        ) {
                            Text(state.error!!, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // ── Bottom nav ────────────────────────────
                    WizardNavRow(
                        onBack = if (state.currentStep > 1) ({ viewModel.previousStep() }) else null,
                        onNext = {
                            if (state.currentStep < state.totalSteps) {
                                viewModel.nextStep()
                            } else {
                                currentUser?.id?.let { viewModel.submitProperty(it) }
                            }
                        },
                        nextLabel = if (state.currentStep == state.totalSteps) Strings.SUBMIT else Strings.NEXT,
                        nextEnabled = !state.isSubmitting,
                        isNextLoading = state.isSubmitting
                    )
                }
            }
        }

        // Close confirmation dialog
        if (showCloseDialog) {
            AlertDialog(
                onDismissRequest = { showCloseDialog = false },
                title = { Text("Discard draft?") },
                text = { Text("Your progress is saved as a draft. You can continue later from Properties.") },
                confirmButton = {
                    TextButton(
                        onClick = { navigator.pop() },
                        colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.AccentOrange)
                    ) { Text("Discard") }
                },
                dismissButton = {
                    TextButton(onClick = { showCloseDialog = false }) { Text("Keep editing") }
                }
            )
        }
    }
}
