package com.kpn.falcon.presentation

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.kpn.falcon.di.SessionManager
import com.kpn.falcon.presentation.screens.LoginScreen
import com.kpn.falcon.presentation.screens.MainScreen
import com.kpn.falcon.presentation.theme.KPNTheme
import org.koin.compose.koinInject

@Composable
fun KPNFalconApp() {
    val sessionManager = koinInject<SessionManager>()
    val startScreen = if (sessionManager.isLoggedIn()) MainScreen else LoginScreen

    KPNTheme {
        Navigator(screen = startScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
