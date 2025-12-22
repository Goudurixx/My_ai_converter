package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme


@Composable
internal fun MyConverterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocused: (Boolean) -> Unit,
    prefix: String,
    label: String,
    currency: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val view = LocalView.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        leadingIcon = {
            Text(
                text = prefix,
                textAlign = TextAlign.Center,
            )
        },
//        placeholder = {
//            Text(
//                text = label,
//                textAlign = TextAlign.Center,
//            )
//        },
        trailingIcon = {
            Text(
                text = currency,
                textAlign = TextAlign.End,
            )
        },
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(textAlign = TextAlign.Center),
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
        ),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource,
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
@Preview
private fun MyConverterTextfieldPreview() {
    MyAIConverterTheme() {
        Column{
            MyConverterTextField(
                value = "Hey",
                onValueChange = {},
                onFocused = { },
                prefix = "Hey",
                label = "Hey",
                currency = "Hey",
                modifier = Modifier,
            )
            MyConverterTextField(
                value = "Hey",
                onValueChange = {},
                onFocused = { },
                prefix = "Hey",
                label = "Hey",
                currency = "Hey",
                modifier = Modifier,
                enabled = false,
            )
        }
    }
}