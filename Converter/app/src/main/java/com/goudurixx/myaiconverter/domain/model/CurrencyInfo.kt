package com.goudurixx.myaiconverter.domain.model

data class CurrencyInfo(
    val code: String,
    val name: String?,
    val rate: Double? = null,
    val selected: Boolean = false,
) {
    fun match(query: String): Boolean =
        code.contains(query, ignoreCase = true) || name?.contains(query, ignoreCase = true) ?: false

}
