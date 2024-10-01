package com.app.currencyconverter.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.currencyconverter.utils.MapTypeConverter

@Entity
data class CurrenciesData(
    @PrimaryKey var id : Int? = 0,
    val base: String?,
    val disclaimer: String?,
    val license: String?,
    @TypeConverters(MapTypeConverter::class) var rates: HashMap<String, Float>?=null,
    val timestamp: Long?
)