package com.goudurixx.myaiconverter.domain.usecase

import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SyncCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository,
) {
    suspend operator fun invoke() {
        repository.syncIfNeeded()
    }

    fun getSyncingState(): StateFlow<Boolean> {
        return repository.isSyncing
    }
}