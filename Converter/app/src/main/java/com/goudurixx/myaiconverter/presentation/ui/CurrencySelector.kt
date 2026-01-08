package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Trim
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme


@Composable
fun CurrencySelector(
    onCurrencyClicked: (CurrencyInfo) -> Unit,
    getCurrencyEmoji: (String) -> String,
    getCurrencySymbol: (String) -> String,
    info: CurrencyInfo,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = {
                    onCurrencyClicked(info)
                },
                enabled = enabled
            )
            .drawWithContent(
                {
                    drawContent()
                    if (enabled.not())
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.2f),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                        )
                }
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = getCurrencyEmoji(info.code),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .widthIn(max = 120.dp)
                .wrapContentWidth(align = Alignment.Start),
        ) {
            Text(
                text = info.code.toUpperCase(Locale.current),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = info.name ?: "",
                maxLines = 2,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${info.rate?.let { "%.2f".format(it) }}",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        SymbolFormatter(
            symbol = getCurrencySymbol(info.code),
            modifier = Modifier.align(Alignment.Bottom),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
@PreviewLightDark
private fun CurrencySelectorPreview() {
    val currency = CurrencyInfo(
        "yili", "yili", rate = 1.220, selected = true,
    )
    MyAIConverterTheme() {
        Column() {
            CurrencySelector(
                onCurrencyClicked = { },
                getCurrencyEmoji = { _ -> "" },
                getCurrencySymbol = { _ -> "$" },
                info = currency,
                enabled = true
            )
            CurrencySelector(
                onCurrencyClicked = { },
                getCurrencyEmoji = { _ -> "" },
                getCurrencySymbol = { _ -> "€" },
                info = currency,
                enabled = false
            )
            CurrencySelector(
                onCurrencyClicked = { },
                getCurrencyEmoji = { _ -> "" },
                getCurrencySymbol = { _ -> "CH" },
                info = currency,
                enabled = false
            )
            CurrencySelector(
                onCurrencyClicked = { },
                getCurrencyEmoji = { _ -> "" },
                getCurrencySymbol = { _ -> "ETH" },
                info = currency,
                enabled = false
            )
        }
    }
}