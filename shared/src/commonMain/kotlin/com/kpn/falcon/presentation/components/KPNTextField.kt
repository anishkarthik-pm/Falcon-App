package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius

/**
 * KPN-branded text input.
 * Design spec: gray background #F5F5F5, 8dp corner, no border until focused.
 * Optional suffix label (sqft, ft, ₹/sqft, etc.).
 * Optional error/deviation flag.
 */
@Composable
fun KPNTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String = "",
    suffix: String = "",
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    isDeviation: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor = when {
        isDeviation -> KPNColors.AccentOrange
        isError -> KPNColors.AccentRed
        isFocused -> KPNColors.AccentGreen
        else -> Color.Transparent
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Label row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label + if (isRequired) " *" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = KPNColors.TextSecondary
            )
        }

        // Input container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isDeviation) KPNColors.DeviationHighlight else KPNColors.Background,
                    shape = KPNRadius.input
                )
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = KPNRadius.input
                )
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused },
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    maxLines = if (singleLine) 1 else maxLines,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = if (enabled) KPNColors.TextPrimary else KPNColors.TextSecondary
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(onAny = { onImeAction() }),
                    visualTransformation = visualTransformation,
                    decorationBox = { innerTextField ->
                        if (value.isEmpty() && hint.isNotBlank()) {
                            Text(hint, style = MaterialTheme.typography.bodyLarge, color = KPNColors.TextSecondary.copy(alpha = 0.6f))
                        }
                        innerTextField()
                    }
                )
                if (suffix.isNotBlank()) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KPNColors.TextSecondary
                    )
                }
            }
        }

        // Deviation or error label below input
        when {
            isDeviation -> DeviationFlag()
            isError && errorMessage.isNotBlank() -> Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.AccentRed
            )
            hint.isNotBlank() && !singleLine -> Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.TextSecondary
            )
        }
    }
}

/**
 * Numeric-only variant — commonly used for area, rent, parking counts.
 */
@Composable
fun KPNNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String = "",
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    isDeviation: Boolean = false,
    inlineHint: String = "",
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        KPNTextField(
            value = value,
            onValueChange = { newVal -> if (newVal.all { it.isDigit() || it == '.' }) onValueChange(newVal) },
            label = label,
            suffix = suffix,
            isRequired = isRequired,
            isError = isError,
            errorMessage = errorMessage,
            isDeviation = isDeviation,
            enabled = enabled,
            keyboardType = KeyboardType.Decimal
        )
        if (inlineHint.isNotBlank()) {
            Text(
                text = inlineHint,
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.AccentOrange,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
