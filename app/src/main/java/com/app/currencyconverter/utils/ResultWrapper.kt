package com.app.currencyconverter.utils




sealed class ResultWrapper<out R> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val exception: LocalisedException) : ResultWrapper<Nothing>()
}
