package com.goudurixx.myaiconverter.domain.usecase

import android.util.Log
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrencySymbol @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    operator fun invoke(code: String): String {
        val l = currencyRepository.getCurrencySymbol(code).getOrElse {
            Log.e("getCurrencySymbolUseCase", it.message ?: "Unknown behavior encountered")
            return "!"
        }
        Log.e("ImplSymp", l)
        return l
    }
}