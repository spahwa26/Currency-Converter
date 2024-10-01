package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.localdb.CurrenciesDatabase
import com.app.currencyconverter.data.localdb.preferences.CurrencyPreferences
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.network.SafeApiRequest
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val database: CurrenciesDatabase,
    private val prefs : CurrencyPreferences
) : SafeApiRequest() {

    suspend fun getCurrencyList() = database.currencyDao().getCurrenciesList()

    suspend fun getShowCurrenciesList() = database.currencyDao().getShowCurrenciesList()

    suspend fun getCurrencyRates() = database.currencyDao().getCurrencyRates()

    suspend fun updateCurrencyInfo(info : CurrencyInfo){
        database.currencyDao().updateCurrenciesInfo(info)
    }

    fun getBaseCurrency()=prefs.baseCurrency

    fun setBaseCurrency(currency: String){
        prefs.baseCurrency=currency
    }
}