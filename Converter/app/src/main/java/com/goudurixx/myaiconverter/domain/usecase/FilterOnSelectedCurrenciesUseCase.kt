package com.goudurixx.myaiconverter.domain.usecase

import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FilterOnSelectedCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): StateFlow<List<CurrencyInfo>> {
        repository.getSelectedCurrencies()
        return repository.selectedCurrenciesFlow
    }
}