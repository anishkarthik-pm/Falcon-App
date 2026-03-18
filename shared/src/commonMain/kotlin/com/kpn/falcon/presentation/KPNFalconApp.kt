package com.kpn.falcon.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.kpn.falcon.presentation.screens.MainScreen
import com.kpn.falcon.presentation.theme.KPNTheme

@Composable
fun KPNFalconApp() {
    KPNTheme {
        Navigator(screen = MainScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
