package com.app.currencyconverter.utils

object Constants {

    init {
        System.loadLibrary("secrets")
    }

    const val NUM_30 = 30L
    const val AUTHORIZATION = "Authorization"
    const val NA = "N/A"
    const val TOKEN = "Token " //the extra space is being used to avoid hardcoded string in adding API key to header
    const val DATABASE_NAME = "CurrencyConverter.db"

    external fun getSecrets(): String
}