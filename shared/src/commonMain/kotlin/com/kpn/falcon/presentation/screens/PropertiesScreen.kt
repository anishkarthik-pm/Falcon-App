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
fun PropertiesScreen() {
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
                    text = Strings.PROPERTIES_TITLE,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = Strings.PROPERTIES_EMPTY_TITLE,
                    style = MaterialTheme.typography.bodyLarge,
                    color = KPNColors.TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Task 4 will build the full Properties List",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.TextSecondary
                )
            }
        }
    }
}
