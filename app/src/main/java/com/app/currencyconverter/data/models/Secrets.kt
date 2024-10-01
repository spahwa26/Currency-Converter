package com.app.currencyconverter.data.models

import com.google.gson.annotations.SerializedName


data class Secrets(
    @SerializedName("base_url") val baseUrl: String,
    @SerializedName("api_key") val apiKey: String
)