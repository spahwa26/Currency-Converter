package com.app.currencyconverter.utils

import android.icu.text.NumberFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import java.io.IOException
import java.util.Locale

val Exception?.localizedException: LocalisedException
    get() {
        return when (this) {
            is LocalisedException -> this
            is IOException -> NoInternetException(message)
            else -> SomethingWentWrongException(this?.localizedMessage)
        }
    }

fun Double.formatAmountWithCommas(decimalPlaces: Int = 2, locale: Locale = Locale.getDefault()): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    numberFormat.maximumFractionDigits = decimalPlaces
    numberFormat.minimumFractionDigits = decimalPlaces
    return numberFormat.format(this)
}

@Composable
fun getColors() = MaterialTheme.colorScheme