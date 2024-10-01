package com.app.currencyconverter.di

import android.content.Context
import com.app.currencyconverter.data.models.Secrets
import com.app.currencyconverter.utils.Constants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecrets(): Secrets {
        val jsonData = Constants.getSecrets()
        return Gson().fromJson(jsonData, Secrets::class.java)
    }

}