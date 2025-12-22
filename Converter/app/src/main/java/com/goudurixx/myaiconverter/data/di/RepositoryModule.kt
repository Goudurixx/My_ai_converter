package com.goudurixx.myaiconverter.data.di

import com.goudurixx.myaiconverter.data.repository.CurrencyRepositoryImpl
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository
}
