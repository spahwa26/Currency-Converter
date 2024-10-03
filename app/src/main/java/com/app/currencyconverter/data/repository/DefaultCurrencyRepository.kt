package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.localdb.CurrenciesDatabase
import com.app.currencyconverter.data.localdb.preferences.CurrencyPreferences
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.network.CurrencyApi
import com.app.currencyconverter.data.network.SafeApiRequest
import com.app.currencyconverter.utils.ResultWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DefaultCurrencyRepository  @Inject constructor(
    private val client: CurrencyApi,
    private val database: CurrenciesDatabase,
    private val prefs: CurrencyPreferences
) : CurrencyRepository, SafeApiRequest() {

    override suspend fun updateCurrencyData(callCurrencyNamesApi: Boolean): ResultWrapper<Unit> =
        suspendCoroutine { coroutine ->
            CoroutineScope(IO).launch {
                val ratesRes = apiRequest { client.getCurrencyData() }
                when (ratesRes) {
                    is ResultWrapper.Success -> {
                        if (callCurrencyNamesApi) {
                            val dataRes = apiRequest { client.getCurrencyInfo() }
                            when (dataRes) {
                                is ResultWrapper.Success -> {
                                    database.currencyDao().insertCurrenciesRates(ratesRes.data)
                                    /**
                                     * the reason behind below API call is to enable search by currency
                                     * name because everyone will not remember the currency code
                                    */
                                    database.currencyDao().insertCurrenciesInfo(dataRes.data.map {
                                        CurrencyInfo(it.key, it.value)
                                    })
                                    coroutine.resume(ResultWrapper.Success(Unit))
                                }

                                is ResultWrapper.Error -> {
                                    coroutine.resume(ResultWrapper.Error(dataRes.exception))
                                }
                            }
                        } else {
                            database.currencyDao().insertCurrenciesRates(ratesRes.data)
                            coroutine.resume(ResultWrapper.Success(Unit))
                        }
                    }

                    is ResultWrapper.Error -> {
                        coroutine.resume(ResultWrapper.Error(ratesRes.exception))
                    }
                }
            }
        }


    override suspend fun getCurrencyList() = database.currencyDao().getCurrenciesList()

    override suspend fun getShowCurrenciesList() = database.currencyDao().getShowCurrenciesList()

    override suspend fun getCurrencyRates() = database.currencyDao().getCurrencyRates()

    override suspend fun getUpdatesCount() = database.currencyDao().getUpdatesCount()

    override suspend fun updateTheCount(count: Int) =
        database.currencyDao().updateCountInRatesTable(count)

    override suspend fun updateCurrencyInfo(info: CurrencyInfo) {
        database.currencyDao().updateCurrenciesInfo(info)
    }

    override fun getBaseCurrency() = prefs.baseCurrency

    override fun setBaseCurrency(currency: String) {
        prefs.baseCurrency = currency
    }

}