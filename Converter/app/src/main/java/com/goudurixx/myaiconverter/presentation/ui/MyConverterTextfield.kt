package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import com.goudurixx.myaiconverter.domain.model.ConversionResult
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme


@Composable
internal fun MyConverterTextField(
    value: ConversionResult,
    onValueChange: (String) -> Unit,
    onFocused: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val view = LocalView.current
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // Use a separate state for the TextField's actual text content
    // Initialize it with the formatted value from the ViewModel
    var textState by remember {
        mutableStateOf(
            value.value?.let { "%.2f".format(it) } ?: ""
        )
    }

    // Update textState when the ViewModel's value changes
    // This handles cases where the value is updated from elsewhere
    LaunchedEffect(value.value) {
        if (value.value != null) {
            val formattedValue = "%.2f".format(value.value)
            if (textState != formattedValue) {
                textState = formattedValue
            }
        } else {
            textState = ""
        }
    }

    TextField(
        value = textState,
        onValueChange = { newValue ->
            // --- Sanitization Logic ---
            val sanitizedValue = newValue.filter { it.isDigit() || it == '.' } // Allow digits and dot
            val parts = sanitizedValue.split(".")
            val finalValue = when {
                parts.size > 2 -> { // More than one dot, take first dot and subsequent digits
                    parts[0] + "." + parts.drop(1).joinToString("")
                }

                parts.size == 2 && parts[1].length > 2 -> { // More than two decimal places, truncate
                    parts[0] + "." + parts[1].take(2)
                }

                else -> sanitizedValue
            }

            textState = finalValue // Update local text state immediately
            onValueChange(finalValue) // Pass the sanitized string to the ViewModel
        },
        enabled = enabled,
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = value.emoji ?: String(),
                    textAlign = TextAlign.Center,
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = value.info.code.toUpperCase(Locale.current),
                    )
                    Text(
                        text = value.info.rate?.let { "%.2f".format(it) } ?: String(),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        trailingIcon = {
            SymbolFormatter(
                symbol = value.symbol ?: String(),
            )
        },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        ),
        colors = TextFieldDefaults.colors().copy(
            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
        ),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    // Handle press start (e.g., trigger haptic feedback or custom logic)
                    view.performHapticFeedback(HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)

                }

                is PressInteraction.Cancel -> {
                    // Handle press cancel
                    view.performHapticFeedback(HapticFeedbackConstantsCompat.REJECT)
                }

                is FocusInteraction.Focus -> {
                    // Handle focus gained
                    view.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
                    onFocused(true)
                }

                is FocusInteraction.Unfocus -> {
                    view.performHapticFeedback(HapticFeedbackConstantsCompat.REJECT)
                    onFocused(false)
                }
            }
        }
    }
}


@Composable
@PreviewLightDark
private fun MyConverterTextfieldPreview() {
    val conversion = ConversionResult(
        info = CurrencyInfo(
            code = "USD",
            name = "US Dollar",
            rate = 1.0,
            selected = true,
        ),
        value = 0.0,
        symbol = "$",
        emoji = "°-°"
    )
    MyAIConverterTheme() {
        Column {
            MyConverterTextField(
                value = conversion,
                onValueChange = {},
                onFocused = { },
                modifier = Modifier,
            )
            MyConverterTextField(
                value = conversion,
                onValueChange = {},
                onFocused = { },
                modifier = Modifier,
                enabled = false,
            )
        }
    }
}