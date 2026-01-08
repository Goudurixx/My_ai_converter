package com.goudurixx.myaiconverter.domain.usecase

import android.util.Log
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import javax.inject.Inject

class SelectCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository,
) {
    suspend operator fun invoke(code: String, isSelected: Boolean) {
        repository.selectCurrency(code = code, isSelected = isSelected)
            .onSuccess {
                Log.e("SelectCurrencyUseCase", "Successfultly updated $code with : $it ")
            }
            .onFailure {
                Log.e("SelectCurrencyUseCase", "Wrongfully didn't update $code with error : ${it.message} ")
            }
    }
}