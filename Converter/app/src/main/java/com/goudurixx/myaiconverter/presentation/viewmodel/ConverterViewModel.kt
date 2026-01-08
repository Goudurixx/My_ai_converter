package com.goudurixx.myaiconverter.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goudurixx.myaiconverter.domain.model.ConversionResult
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.usecase.ConvertPriceUseCase
import com.goudurixx.myaiconverter.domain.usecase.FilterOnSelectedCurrenciesUseCase
import com.goudurixx.myaiconverter.domain.usecase.GetCurrenciesUseCase
import com.goudurixx.myaiconverter.domain.usecase.GetCurrencyEmoji
import com.goudurixx.myaiconverter.domain.usecase.GetCurrencySymbol
import com.goudurixx.myaiconverter.domain.usecase.SyncCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    val convertPriceUseCase: ConvertPriceUseCase,
    val syncCurrenciesUseCase: SyncCurrenciesUseCase,
    val getCurrenciesUseCase: GetCurrenciesUseCase,
    val filterOnSelectedCurrenciesUseCase: FilterOnSelectedCurrenciesUseCase,
    val getCurrencyEmoji: GetCurrencyEmoji,
    val getCurrencySymbol: GetCurrencySymbol,
) : ViewModel() {

    val loading: StateFlow<Boolean> = syncCurrenciesUseCase.getSyncingState()

    private val _currencies: MutableStateFlow<List<ConversionResult>> = MutableStateFlow(emptyList())
    val currencies: StateFlow<List<ConversionResult>> = _currencies.asStateFlow()

    private var input: Double? = null

    init {
        viewModelScope.launch {
            syncCurrenciesUseCase.invoke()
        }
    }

    suspend fun onReload() {
        filterOnSelectedCurrenciesUseCase.invoke().map { currencies ->
            currencies.map { currency ->
                ConversionResult(
                    info = currency,
                    value = input?.let { currency.rate?.times(it) },
                    symbol = getCurrencySymbol(currency.code),
                    emoji = getCurrencyEmoji.invoke(currency.code)
                )
            }
        }.collect { list ->
            Log.e("_ViewModel", "here $list")
            _currencies.update {
                list
            }
        }
    }

    fun onUpdate(info: CurrencyInfo, newInp: String) {
        val double = newInp.toDoubleOrNull()
        if (double != null) {
            input = if (double == 0.00) {
                null
            } else {
                info.rate?.let { double / it }
            }
        }

        _currencies.update {
            currencies.value.map {
                it.copy(
                    value = it.info.rate?.let {
                        input?.times(it)
                    }
                )
            }
        }
    }
}
