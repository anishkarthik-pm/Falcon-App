package com.kpn.falcon.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNTheme
import com.kpn.falcon.util.Strings

@Composable
fun HomeScreen() {
    KPNTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = KPNColors.Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Strings.APP_NAME,
                    style = MaterialTheme.typography.headlineLarge,
                    color = KPNColors.PrimaryText
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Home — ${Strings.HOME_DASHBOARD}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = KPNColors.TextSecondary
                )
                Spacer(Modifier.height(24.dp))
                Surface(
                    color = KPNColors.Primary,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Scaffold Ready — Task 3 will build full Home Screen",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = KPNColors.PrimaryText
                    )
                }
            }
        }
    }
}
