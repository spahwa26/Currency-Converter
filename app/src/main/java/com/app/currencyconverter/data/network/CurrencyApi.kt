package com.app.currencyconverter.data.network

import com.app.currencyconverter.data.models.CurrenciesData
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApi {
    @GET("latest.json/")
    suspend fun getCurrencyData(): Response<CurrenciesData>

    @GET("currencies.json/")
    suspend fun getCurrencyInfo(): Response<HashMap<String, String>>
}
