package com.app.currencyconverter.data.localdb.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CurrencyPreferences @Inject constructor(@ApplicationContext private val context : Context) {
    private val prefManager by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    var baseCurrency: String
        get() = prefManager.getString(DEFAULT_CURRENCY, USD)?:USD
        set(value) = prefManager.edit().putString(DEFAULT_CURRENCY, value).apply()

    companion object{
        const val DEFAULT_CURRENCY = "DEFAULT_CURRENCY"
        const val USD = "USD"
    }
}