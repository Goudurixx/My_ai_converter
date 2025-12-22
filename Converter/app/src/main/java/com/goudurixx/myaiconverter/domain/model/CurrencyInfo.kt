package com.goudurixx.myaiconverter.domain.model

data class CurrencyInfo(
    val code: String,
    val name: String?,
    val rate: Double = -1.0,
) {
    fun match(query: String): Boolean =
        code.contains(query, ignoreCase = true) || name?.contains(query, ignoreCase = true) ?: false
}
