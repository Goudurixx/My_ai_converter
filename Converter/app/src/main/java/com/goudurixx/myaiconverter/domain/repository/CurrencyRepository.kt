package com.goudurixx.myaiconverter.domain.repository

import com.goudurixx.myaiconverter.domain.model.CurrencyInfo

interface CurrencyRepository {
    suspend fun syncCurrencies(): Result<Boolean>
    suspend fun getCurrencyName(code: String): String
    suspend fun getFilteredCurrencies(query: String?): Result<List<CurrencyInfo>>
    suspend fun getEuroRate(target: String): Result<Double>
}
