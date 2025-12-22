package com.goudurixx.myaiconverter.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goudurixx.myaiconverter.domain.usecase.ConvertPriceUseCase
import com.goudurixx.myaiconverter.presentation.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    val convertPriceUseCase: ConvertPriceUseCase,
) : ViewModel() {

    var _currencies = MutableStateFlow(
        listOf(
            Currency(
                name = "\uD83C\uDDEA\uD83C\uDDFA",
                currencyCode = "EUR",
                rate = 1.0,
            ),
            Currency(
                name = "\uD83C\uDDF5\uD83C\uDDF1",
                currencyCode = "PLN",
                rate = -1.0,
            ),
            Currency(
                name = "\uD83C\uDDFA\uD83C\uDDE6",
                currencyCode = "UAH",
                rate = -1.0,
            )
        )
    )
    val currencies = _currencies.asStateFlow()

    init {
        viewModelScope.launch {
            _currencies.update {
                it.map { currency ->
                    if (currency.rate < 0.0) {
                        var newCurrency = currency
                        convertPriceUseCase.invoke(currency.currencyCode)
                            .onSuccess {
                                newCurrency = currency.copy(
                                    rate = it
                                )
                            }
                            .onFailure {
                                Log.e("ViewModel", "failed for ${currency.currencyCode} : ${it.message}")
                            }
                        newCurrency
                    } else
                        currency
                }
            }
        }
    }

    var euroInput by mutableStateOf("")
        private set

    var plnInput by mutableStateOf("")
        private set

    var uahInput by mutableStateOf("")
        private set

    fun updateEuro(input: String) {
        euroInput = input
        val euro = input.toDoubleOrNull()
        if (euro != null) {

            plnInput = "%.2f".format(euro * currencies.value[1].rate)
            uahInput = "%.2f".format(euro * currencies.value[2].rate)
        } else {
            plnInput = ""
            uahInput = ""
        }
    }

    fun updatePln(input: String) {
        plnInput = input
        val pln = input.toDoubleOrNull()
        if (pln != null) {
            euroInput = "%.2f".format(pln / currencies.value[1].rate)
            uahInput = "%.2f".format(pln / currencies.value[1].rate * currencies.value[2].rate)
        } else {
            euroInput = ""
            uahInput = ""
        }
    }

    fun updateUah(input: String) {
        uahInput = input
        val uah = input.toDoubleOrNull()
        if (uah != null) {
            euroInput = "%.2f".format(uah / currencies.value[2].rate)
            plnInput = "%.2f".format(uah / currencies.value[2].rate * currencies.value[1].rate)
        } else {
            euroInput = ""
            plnInput = ""
        }
    }
}
