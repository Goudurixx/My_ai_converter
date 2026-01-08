package com.goudurixx.myaiconverter.domain.model

data class ConversionResult(
    val info: CurrencyInfo,
    val value: Double?,
    val symbol: String? = null,
    val emoji: String? = null,
)
