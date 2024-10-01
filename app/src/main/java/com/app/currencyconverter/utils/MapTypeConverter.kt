package com.app.currencyconverter.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MapTypeConverter {
    @TypeConverter
    fun fromString(value: String): HashMap<String, Float> {
        val mapType = object : TypeToken<HashMap<String, Float>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromHashMap(map: HashMap<String, Float>): String {
        return Gson().toJson(map)
    }
}