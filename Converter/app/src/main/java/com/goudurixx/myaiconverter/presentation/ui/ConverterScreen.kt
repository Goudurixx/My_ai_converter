package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goudurixx.myaiconverter.domain.model.ConversionResult
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.presentation.viewmodel.ConverterViewModel
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme
import kotlin.math.max

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: ConverterViewModel = hiltViewModel()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onReload()
    }
    ConverterScreen(
        currencies = currencies,
        onUpdate = viewModel::onUpdate,
        modifier = modifier,
    )
}

@Composable
private fun ConverterScreen(
    currencies: List<ConversionResult>,
    onUpdate: (CurrencyInfo, String) -> Unit,
    modifier: Modifier = Modifier,
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
        modifier = modifier.fillMaxSize()
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
                text = (currencies.map { it.symbol }).joinToString(" ").let {
                    it.repeat((150 / ((max(1,it.length))).toInt() ))
                },
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
            LazyColumn(
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
                items(
                    items = currencies
                ) { conversion ->
                    MyConverterTextField(
                        value = conversion,
                        onValueChange = {
                            onUpdate(conversion.info, it)
                        },
                        onFocused = { focused = it },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PreviewConverterScreen() {
    val currencies = listOf(
        ConversionResult(
            CurrencyInfo(
                name = "\uD83C\uDDEA\uD83C\uDDFA",
                code = "EUR",
                rate = 1.0,
            ), value = 0.0
        ),
        ConversionResult(
            CurrencyInfo(
                name = "\uD83C\uDDF5\uD83C\uDDF1",
                code = "PLN",
                rate = -1.0,
            ), value = 0.0
        ),
        ConversionResult(
            CurrencyInfo(
                name = "\uD83C\uDDFA\uD83C\uDDE6",
                code = "UAH",
                rate = -1.0,
            ), value = 0.0
        ),
    )
    MyAIConverterTheme {
        ConverterScreen(
            currencies = currencies,
            onUpdate = { _, _ -> }
        )
    }
}
