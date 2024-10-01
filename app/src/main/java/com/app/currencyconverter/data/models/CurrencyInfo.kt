package com.app.currencyconverter.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyInfo(
    @PrimaryKey val code: String,
    val countryName: String
)
