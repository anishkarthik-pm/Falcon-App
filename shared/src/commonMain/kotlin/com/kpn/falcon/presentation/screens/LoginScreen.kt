package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.presentation.viewmodels.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

object LoginScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<LoginViewModel>()
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(state.isLoggedIn) {
            if (state.isLoggedIn) {
                navigator.replace(MainScreen)
            }
        }

        KPNTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = KPNColors.Background) {
                LoginContent(
                    email = state.email,
                    password = state.password,
                    isLoading = state.isLoading,
                    error = state.error,
                    onEmailChange = viewModel::onEmailChanged,
                    onPasswordChange = viewModel::onPasswordChanged,
                    onLogin = viewModel::login
                )
            }
        }
    }
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))

        // Logo / Brand area
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(KPNColors.Primary, KPNRadius.card),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "KPN",
                fontSize = 22.sp,
                style = MaterialTheme.typography.headlineLarge,
                color = KPNColors.PrimaryText
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "KPN Falcon",
            style = MaterialTheme.typography.headlineLarge,
            color = KPNColors.TextPrimary
        )
        Text(
            text = "Business Development Platform",
            style = MaterialTheme.typography.bodyLarge,
            color = KPNColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        // Email
        KPNTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            hint = "you@kpnfarmfresh.com",
            isRequired = true,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Password
        KPNTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            hint = "Enter your password",
            isRequired = true,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onImeAction = onLogin,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Show/hide password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { passwordVisible = !passwordVisible },
                colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.AccentOrange)
            ) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    if (passwordVisible) "Hide" else "Show",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Error
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KPNColors.AccentRed.copy(alpha = 0.08f), KPNRadius.input)
                    .padding(12.dp)
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.AccentRed
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        KPNPrimaryButton(
            text = "Sign In",
            onClick = onLogin,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "KPN Farm Fresh · Internal Platform",
            style = MaterialTheme.typography.labelSmall,
            color = KPNColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
