package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Trim
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme


@Composable
fun SymbolFormatter(
    symbol: String,
    color: Color? = null,
    modifier: Modifier = Modifier
) {
    Text(
        text = symbol,
        modifier = modifier,
        textAlign = TextAlign.End,
        maxLines = 1,
        style = MaterialTheme.typography.headlineSmall.let {
            it.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = it.fontSize / symbol.length,
                lineHeight = it.lineHeight / symbol.length,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = Trim.None,
                ),
                baselineShift = BaselineShift(1f * (symbol.length - 0.8f))
            )
        },
    )
}

@Composable
@PreviewLightDark
private fun SymbolFormatrerPreview() {
    MyAIConverterTheme() {
    val color = MaterialTheme.colorScheme.primaryContainer
        Column(
            Modifier.background(color)
        ) {
            SymbolFormatter(
                symbol = "€",
                color = color
            )
            SymbolFormatter(
                symbol = "$",
                color = color


            )
            SymbolFormatter(
                symbol = "CH",
                color = color

            )
            SymbolFormatter(
                symbol = "ETH",
                color = color

            )
        }
    }
}