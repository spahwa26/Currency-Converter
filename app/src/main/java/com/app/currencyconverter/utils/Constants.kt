package com.app.currencyconverter.utils

object Constants {

    init {
        System.loadLibrary("secrets")
    }

    const val NUM_30 = 30L
    const val AUTHORIZATION = "Authorization"
    const val CURRENCY_WORKER = "CURRENCY_WORKER"
    const val AMOUNT_REGEX = "^\\d*\\.?\\d{0,2}$"
    const val TOKEN = "Token " //the extra space is being used to avoid hardcoded string in adding API key to header
    const val DATABASE_NAME = "CurrencyConverter.db"
    const val UPDATE_COUNT = "update_count"
    const val ROW_ID = "row_id"
    const val COUNTRY_NAME = "country_name"
    const val IS_SELECTED = "is_selected"
    const val EMPTY_STRING = ""

    external fun getSecrets(): String
}