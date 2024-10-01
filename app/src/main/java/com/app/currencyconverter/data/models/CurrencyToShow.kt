package com.app.currencyconverter.data.models

import androidx.room.Entity
import androidx.room.Ignore

@Entity
data class CurrencyToShow(
    val code: String,
    val countryName: String,
    @Ignore val currencyValue: Float=0f,
    @Ignore val convertedValue: Double=0.0,
    val isSelected: Boolean=false
)
