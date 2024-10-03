package com.app.currencyconverter.data.repository

import androidx.annotation.VisibleForTesting
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.utils.ResultWrapper
import com.app.currencyconverter.utils.localizedException

class FakeRepository : CurrencyRepository {

    private var shouldReturnError = false

    private var shouldCallApi = false

    private var currenciesToShowData = mutableListOf<CurrencyInfo>()

    fun shouldShowError(){
        shouldReturnError=true
    }

    fun shouldCallApi(){
        shouldCallApi=true
    }

    override suspend fun updateCurrencyData(callCurrencyNamesApi: Boolean): ResultWrapper<Unit> {
        return if (shouldReturnError) ResultWrapper.Error(Exception().localizedException)
        else ResultWrapper.Success(Unit)
    }

    override suspend fun getCurrencyList(): List<CurrencyInfo> {
        return currenciesToShowData
    }

    override suspend fun getShowCurrenciesList(): List<CurrencyInfo> {
        return if (shouldCallApi) emptyList()
        else listOf(CurrencyInfo("INR", "Indian Rupee", false))
    }

    override suspend fun getCurrencyRates(): CurrenciesData? {
        return if (shouldCallApi) null
        else CurrenciesData(0, "", "", "", 0, HashMap<String, Float>().apply {
            put("INR", 1f)
        }, 11)
    }

    override suspend fun getUpdatesCount(): Int? {
        return null
    }

    override suspend fun updateTheCount(count: Int) {
    }

    override suspend fun updateCurrencyInfo(info: CurrencyInfo) {
    }

    override fun getBaseCurrency(): String {
        return ""
    }

    override fun setBaseCurrency(currency: String) {
    }

    //todo: remove is not in use
    @VisibleForTesting
    fun addCurrencies(vararg tasks: CurrencyInfo) {
        for (task in tasks) {
            currenciesToShowData.add(task)
        }
    }
}