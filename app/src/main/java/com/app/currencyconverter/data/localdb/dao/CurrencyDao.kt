package com.app.currencyconverter.data.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateCurrenciesRates(ratesData: CurrenciesData)

    @Query("SELECT * FROM CurrenciesData WHERE id = 0 LIMIT 1")
    fun getCurrencyRates(): CurrenciesData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateCurrenciesInfo(currencyInfo: List<CurrencyInfo>)

    @Query("SELECT * FROM CurrencyInfo ORDER BY code")
    fun getCurrenciesList(): List<CurrencyInfo>

    @Query("SELECT * FROM CurrencyInfo ORDER BY code WHERE")
    fun getShowCurrenciesList(): List<CurrencyInfo>

}