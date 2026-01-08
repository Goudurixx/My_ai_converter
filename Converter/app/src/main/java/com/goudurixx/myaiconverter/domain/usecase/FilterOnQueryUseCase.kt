package com.goudurixx.myaiconverter.domain.usecase

import android.util.Log
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class FilterOnQueryUseCase @Inject constructor(
    private val repository: CurrencyRepository,
) {

    suspend operator fun invoke(query: String?) {
        Log.e("FilterOnQueryUseCase", "query : $query")
        repository.filterCurrencies(query)
    }
}