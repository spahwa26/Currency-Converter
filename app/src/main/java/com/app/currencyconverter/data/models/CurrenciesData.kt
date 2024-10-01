package com.app.currencyconverter.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.currencyconverter.utils.Constants.ROW_ID
import com.app.currencyconverter.utils.Constants.UPDATE_COUNT
import com.app.currencyconverter.utils.MapTypeConverter

@Entity
data class CurrenciesData(
    @PrimaryKey @ColumnInfo(name = ROW_ID, defaultValue = "0") val id : Int,
    val base: String?,
    val disclaimer: String?,
    val license: String?,
    @ColumnInfo(name = UPDATE_COUNT) val updateCount: Int = 0,
    @TypeConverters(MapTypeConverter::class) var rates: HashMap<String, Float>?=null,
    val timestamp: Long?
)