package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goudurixx.myaiconverter.presentation.Currency
import com.goudurixx.myaiconverter.presentation.viewmodel.ConverterViewModel
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme

@Composable
fun ConverterScreen() {
    val viewModel: ConverterViewModel = hiltViewModel()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()
    ConverterScreen(
        currencies = currencies,
        euroInput = viewModel.euroInput,
        plnInput = viewModel.plnInput,
        uahInput = viewModel.uahInput,
        updateEuro = viewModel::updateEuro,
        updatePln = viewModel::updatePln,
        updateUah = viewModel::updateUah,
    )

}

@Composable
private fun ConverterScreen(
    currencies: List<Currency>,
    euroInput: String,
    plnInput: String,
    uahInput: String,
    updateEuro: (String) -> Unit,
    updatePln: (String) -> Unit,
    updateUah: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var focused by remember {
        mutableStateOf(false)
    }
    val alpha by animateFloatAsState(
        targetValue = if (focused) 0.5f else 1F,
        animationSpec = tween(durationMillis = 500), // Adjust duration as needed
        label = "alpha"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = {
                        // Clear focus and hide keyboard when the Box is clicked
                        focusManager.clearFocus()
                        focused = false
                        keyboardController?.hide()
                    },
                    indication = null,
                    interactionSource = null,
                )
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (" €  zł  ₴ ").repeat(50),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                ),
                modifier = Modifier
                    .rotate(45f)
                    .graphicsLayer(
                        {
                            val zoom = 2f + alpha
                            clip = false
                            this.scaleX = zoom
                            this.scaleY = zoom
                        }
                    )
                    .blur(if (focused) 8.dp else 2.dp)
            )
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
                    .graphicsLayer(
                        {
                            val zoom = 1f + 0.1f * alpha
                            scaleX = zoom
                            scaleY = zoom
                        }
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyConverterTextField(
                    value = euroInput,
                    onValueChange = { updateEuro(it) },
                    onFocused = { focused = it },
                    prefix = currencies[0].name,
                    label = currencies[0].rate.toString(),
                    currency = currencies[0].currencyCode,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                MyConverterTextField(
                    value = plnInput,
                    onValueChange = { updatePln(it) },
                    onFocused = { focused = it },
                    prefix = currencies[1].name,
                    label = currencies[1].rate.toString(),
                    currency = currencies[1].currencyCode,
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                MyConverterTextField(
                    value = uahInput,
                    onValueChange = { updateUah(it) },
                    onFocused = { focused = it },
                    prefix = currencies[2].name,
                    label = currencies[2].rate.toString(),
                    currency = currencies[2].currencyCode,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewConverterScreen() {
    val currencies = listOf(
        Currency(
            name = "\uD83C\uDDEA\uD83C\uDDFA",
            currencyCode = "EUR",
            rate = 1.0,
        ),
        Currency(
            name = "\uD83C\uDDF5\uD83C\uDDF1",
            currencyCode = "PLN",
            rate = -1.0,
        ),
        Currency(
            name = "\uD83C\uDDFA\uD83C\uDDE6",
            currencyCode = "UAH",
            rate = -1.0,
        )
    )
    MyAIConverterTheme {
        ConverterScreen(
            currencies = currencies,
            euroInput = "123",
            plnInput = "400",
            uahInput = "2000",
            updateEuro = {},
            updatePln = {},
            updateUah = {},
        )
    }
}
