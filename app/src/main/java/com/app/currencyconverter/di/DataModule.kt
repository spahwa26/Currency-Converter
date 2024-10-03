package com.app.currencyconverter.di

import com.app.currencyconverter.data.models.Secrets
import com.app.currencyconverter.data.repository.CurrencyRepository
import com.app.currencyconverter.data.repository.DefaultCurrencyRepository
import com.app.currencyconverter.utils.Constants
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSecrets(): Secrets {
        val jsonData = Constants.getSecrets()
        return Gson().fromJson(jsonData, Secrets::class.java)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultCurrencyRepository): CurrencyRepository
}