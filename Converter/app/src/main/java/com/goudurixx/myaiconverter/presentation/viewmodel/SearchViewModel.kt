package com.goudurixx.myaiconverter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.usecase.FilterOnQueryUseCase
import com.goudurixx.myaiconverter.domain.usecase.GetCurrenciesUseCase
import com.goudurixx.myaiconverter.domain.usecase.GetCurrencyEmoji
import com.goudurixx.myaiconverter.domain.usecase.GetCurrencySymbol
import com.goudurixx.myaiconverter.domain.usecase.HasSelectedCurrenciesUseCase
import com.goudurixx.myaiconverter.domain.usecase.SelectCurrencyUseCase
import com.goudurixx.myaiconverter.domain.usecase.SyncCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrencyUseCase: GetCurrenciesUseCase,
    private val getSelectedCurrenciesUseCase: HasSelectedCurrenciesUseCase,
    private val searchUseCase: FilterOnQueryUseCase,
    private val syncCurrenciesUseCase: SyncCurrenciesUseCase,
    private val selectCurrencyUseCase: SelectCurrencyUseCase,
    private val getCurrencyEmoji: GetCurrencyEmoji,
    private val getCurrencySymbol: GetCurrencySymbol,
) : ViewModel() {

    val loading: StateFlow<Boolean> = syncCurrenciesUseCase.getSyncingState()
    private val _query: MutableStateFlow<String?> = MutableStateFlow(null)
    val query = _query.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    @OptIn(FlowPreview::class)
    val currencies: StateFlow<List<CurrencyInfo>> = getCurrencyUseCase.invoke()

    init {
        viewModelScope.launch {
            syncCurrenciesUseCase.invoke()
        }
    }

    @OptIn(FlowPreview::class)
    fun onQueryChanged(query: String) {
        viewModelScope.launch {
            _query.update {
                query
            }
            searchUseCase.invoke(_query.value)
        }
    }

    fun selectCurrency(currencyInfo: CurrencyInfo) {
        viewModelScope.launch {
            selectCurrencyUseCase.invoke(
                currencyInfo.code,
                currencyInfo.selected.not()
            )
        }
    }

    fun getCurrencyEmoji(code: String): String = getCurrencyEmoji.invoke(code)
    fun getCurrencySymbol(code: String): String = getCurrencySymbol.invoke(code)

}