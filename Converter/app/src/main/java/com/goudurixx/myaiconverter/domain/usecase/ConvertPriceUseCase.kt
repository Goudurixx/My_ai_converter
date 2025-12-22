package com.goudurixx.myaiconverter.domain.usecase

import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class ConvertPriceUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(target: String): Result<Double> {
        return repository.getEuroRate(target)
    }
}