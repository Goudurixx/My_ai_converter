package com.goudurixx.myaiconverter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goudurixx.myaiconverter.domain.usecase.HasSelectedCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val hasSelectedCurrenciesUseCase: HasSelectedCurrenciesUseCase,
) : ViewModel() {

    // Use a MutableStateFlow to hold the starting page state
    private val _startingPage: MutableStateFlow<Destination> =
        MutableStateFlow(Destination.SearchScreen) // Default to SearchScreen
    val startingPage: StateFlow<Destination> = _startingPage.asStateFlow()

    init {
        // Launch a coroutine to call the suspend use case
        viewModelScope.launch {
            val hasSelected = hasSelectedCurrenciesUseCase.invoke()
            if (hasSelected) {
                _startingPage.value = Destination.Converter // Navigate to Converter if currencies are selected
            } else {
                _startingPage.value = Destination.SearchScreen // Stay on SearchScreen if no currencies selected
            }
        }
    }
}

// Assuming Destination enum is defined as:
// enum class Destination {
//     Converter,
//     SearchScreen;
// }