package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.localdb.CurrenciesDatabase
import com.app.currencyconverter.data.localdb.preferences.CurrencyPreferences
import com.app.currencyconverter.data.models.CurrencyInfo
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val database: CurrenciesDatabase,
    private val prefs: CurrencyPreferences
) {

    suspend fun getCurrencyList() = database.currencyDao().getCurrenciesList()

    suspend fun getShowCurrenciesList() = database.currencyDao().getShowCurrenciesList()

    suspend fun getCurrencyRates() = database.currencyDao().getCurrencyRates()

    suspend fun getUpdatesCount() = database.currencyDao().getUpdatesCount()

    suspend fun updateTheCount(count: Int) = database.currencyDao().updateCountInRatesTable(count)

    suspend fun updateCurrencyInfo(info: CurrencyInfo) {
        database.currencyDao().updateCurrenciesInfo(info)
    }

    fun getBaseCurrency() = prefs.baseCurrency

    fun setBaseCurrency(currency: String) {
        prefs.baseCurrency = currency
    }
}