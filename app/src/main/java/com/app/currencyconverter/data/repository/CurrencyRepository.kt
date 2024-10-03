package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.utils.ResultWrapper

interface CurrencyRepository {

    suspend fun updateCurrencyData(callCurrencyNamesApi: Boolean = true): ResultWrapper<Unit>

    suspend fun getCurrencyList(): List<CurrencyInfo>

    suspend fun getShowCurrenciesList(): List<CurrencyInfo>

    suspend fun getCurrencyRates(): CurrenciesData?

    suspend fun getUpdatesCount(): Int?

    suspend fun updateTheCount(count: Int)

    suspend fun updateCurrencyInfo(info: CurrencyInfo)

    fun getBaseCurrency(): String

    fun setBaseCurrency(currency: String)

}