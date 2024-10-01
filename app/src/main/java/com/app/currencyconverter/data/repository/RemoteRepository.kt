package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.localdb.CurrenciesDatabase
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

class RemoteRepository @Inject constructor(
    private val client: CurrencyApi,
    private val database: CurrenciesDatabase
) : SafeApiRequest() {

    suspend fun updateCurrencyData(): ResultWrapper<Unit> =
        suspendCoroutine { coroutine ->
            CoroutineScope(IO).launch {
                val infoRes = apiRequest { client.getCurrencyInfo() }
                when (infoRes) {
                    is ResultWrapper.Success -> {
                        val dataRes = apiRequest { client.getCurrencyData() }
                        when (dataRes) {
                            is ResultWrapper.Success -> {
                                database.currencyDao().insertUpdateCurrenciesRates(dataRes.data)
                                database.currencyDao().insertCurrenciesInfo(infoRes.data.map {
                                    CurrencyInfo(it.key, it.value)
                                })
                                coroutine.resume(ResultWrapper.Success(Unit))
                            }

                            is ResultWrapper.Error -> {
                                coroutine.resume(ResultWrapper.Error(dataRes.exception))
                            }
                        }
                    }

                    is ResultWrapper.Error -> {
                        coroutine.resume(ResultWrapper.Error(infoRes.exception))
                    }
                }
            }
        }

}