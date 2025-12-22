package com.goudurixx.myaiconverter.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.usecase.GetCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: GetCurrenciesUseCase,
) : ViewModel() {

    private val _query: MutableStateFlow<String?> = MutableStateFlow(null)
    val query = _query.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val _currencies: MutableStateFlow<List<CurrencyInfo>> = MutableStateFlow(emptyList())

    @OptIn(FlowPreview::class)
    val currencies: StateFlow<List<CurrencyInfo>> = combine(_query, _currencies) { query, currencies ->
        query?.let {
            currencies.filter {
                it.match(query)
            }
        } ?: currencies
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList(),
    )

    init {
        viewModelScope.launch {
            _currencies.update { getCurrencies(null) }
        }
    }

    @OptIn(FlowPreview::class)
    fun onQueryChanged(query: String) {
        viewModelScope.launch {
            _query.update {
                query
            }
        }
    }

    private suspend fun getCurrencies(query: String?): List<CurrencyInfo> {
        searchUseCase.invoke(query)
            .onSuccess { result ->
                Log.e("SearchViewModel", result.toString())
                return result
            }.onFailure {
                Log.e("SearchViewModel", it.message.toString())
                throw it
            }
        throw Throwable("hoy hoy hoy")
    }
}