package com.app.currencyconverter.data.models

data class CurrencyToShow(
    val code: String,
    val countryName: String,
    val currencyValue: Float=0f,
    val convertedValue: Double=0.0,
    var isSelected: Boolean=false
)
