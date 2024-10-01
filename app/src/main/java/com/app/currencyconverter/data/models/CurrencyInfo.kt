package com.app.currencyconverter.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.app.currencyconverter.utils.Constants.COUNTRY_NAME
import com.app.currencyconverter.utils.Constants.IS_SELECTED
import com.app.currencyconverter.utils.Constants.UPDATE_COUNT

@Entity
data class CurrencyInfo(
    @PrimaryKey val code: String,
    @ColumnInfo(name = COUNTRY_NAME) val countryName: String,
    @ColumnInfo(name = IS_SELECTED, defaultValue = "true") var isSelected: Boolean = true
)
