package com.app.currencyconverter.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

fun Double.formatAmountWithCommas(
    decimalPlaces: Int = 2, locale: Locale = Locale.getDefault()
): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    numberFormat.maximumFractionDigits = decimalPlaces
    numberFormat.minimumFractionDigits = decimalPlaces
    return numberFormat.format(this)
}

fun Context.finishActivity() {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.finish()
        context = context.baseContext
    }
}

@Composable
fun getColors() = MaterialTheme.colorScheme