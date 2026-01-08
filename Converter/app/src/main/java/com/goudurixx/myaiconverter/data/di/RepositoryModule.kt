package com.goudurixx.myaiconverter.data.di

import com.goudurixx.myaiconverter.data.repository.CurrencyRepositoryImpl
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository = impl
}
