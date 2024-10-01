package com.app.currencyconverter.data.localdb

import android.content.Context
import androidx.room.Room
import com.app.currencyconverter.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, CurrenciesDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration().build()
}