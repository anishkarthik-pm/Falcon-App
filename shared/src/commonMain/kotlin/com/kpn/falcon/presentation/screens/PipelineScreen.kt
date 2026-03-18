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
fun PipelineScreen() {
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
                    text = Strings.TAB_PIPELINE,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Kanban Pipeline — coming in Task 3",
                    style = MaterialTheme.typography.bodyLarge,
                    color = KPNColors.TextSecondary
                )
            }
        }
    }
}
