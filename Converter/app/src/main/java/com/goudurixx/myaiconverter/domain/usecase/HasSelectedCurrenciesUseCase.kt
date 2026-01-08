package com.goudurixx.myaiconverter.domain.usecase

import com.goudurixx.myaiconverter.core.DEFAULT_SELECTED_CURRENCIES
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class HasSelectedCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository,
) {

    suspend operator fun invoke(): Boolean {
        return repository.getSelectedCurrencies().getOrNull()?.let {
            it > DEFAULT_SELECTED_CURRENCIES
        } ?: false
    }
}