package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.localdb.CurrenciesDatabase
import com.app.currencyconverter.data.localdb.preferences.CurrencyPreferences
import com.app.currencyconverter.data.network.SafeApiRequest
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val database: CurrenciesDatabase,
    private val prefs : CurrencyPreferences
) : SafeApiRequest() {

    fun getCurrencyList() = database.currencyDao().getCurrenciesList()

    fun getCurrencyRates() = database.currencyDao().getCurrencyRates()

    fun getBaseCurrency()=prefs.baseCurrency

    fun setBaseCurrency(currency: String){
        prefs.baseCurrency=currency
    }
}