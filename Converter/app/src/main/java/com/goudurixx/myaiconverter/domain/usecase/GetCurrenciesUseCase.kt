package com.goudurixx.myaiconverter.domain.usecase

import android.util.Log
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(query: String?): Result<List<CurrencyInfo>> {
        Log.e("GetCurrencyUseCase", "query : $query")
        return if (repository.syncCurrencyNames().getOrNull() != null) {
            repository.getFilteredCurrencies(query)
        } else
            throw Throwable()
    }
}
