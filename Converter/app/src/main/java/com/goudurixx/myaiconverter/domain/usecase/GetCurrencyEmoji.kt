package com.goudurixx.myaiconverter.domain.usecase

import android.util.Log
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrencyEmoji @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    operator fun invoke(code: String): String {
        return currencyRepository.getCurrencyEmoji(code).getOrElse {
            Log.e("GetCurrencyEmojiUseCase", it.message ?: "Unknown behavior encountered")
            return "!"
        }
    }
}