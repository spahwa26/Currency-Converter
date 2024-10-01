package com.app.currencyconverter.data.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.localdb.dao.CurrencyDao
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.utils.MapTypeConverter

@Database(entities = [CurrenciesData::class, CurrencyInfo::class], version = 1, exportSchema = false)
@TypeConverters(MapTypeConverter::class)
abstract class CurrenciesDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}