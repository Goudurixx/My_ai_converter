package com.goudurixx.myaiconverter.domain.repository

import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import kotlinx.coroutines.flow.StateFlow

interface CurrencyRepository {
    suspend fun filterCurrencies(query: String?)
    suspend fun getEuroRate(target: String): Result<Double>
    suspend fun getSelectedCurrencies(): Result<Int>
    val isSyncing: StateFlow<Boolean>
    suspend fun syncIfNeeded()
    suspend fun selectCurrency(code: String, isSelected: Boolean): Result<Boolean>
    fun getCurrencyEmoji(code: String): Result<String>
    fun getCurrencySymbol(code: String): Result<String>
    val currenciesFlow: StateFlow<List<CurrencyInfo>>
    val selectedCurrenciesFlow: StateFlow<List<CurrencyInfo>>
}
