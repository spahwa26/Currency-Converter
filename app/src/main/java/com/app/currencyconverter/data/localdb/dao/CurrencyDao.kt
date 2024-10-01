package com.app.currencyconverter.data.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrenciesRates(ratesData: CurrenciesData)

    @Query("SELECT * FROM CurrenciesData WHERE row_id = 0 LIMIT 1")
    suspend fun getCurrencyRates(): CurrenciesData?

    @Query("SELECT update_count FROM CurrenciesData WHERE row_id = 0 LIMIT 1")
    suspend fun getUpdatesCount(): Int?

    @Query("UPDATE CurrenciesData SET update_count = :count WHERE row_id = 0")
    suspend fun updateCountInRatesTable(count: Int): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCurrenciesInfo(currencyInfo: List<CurrencyInfo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrenciesInfo(currencyInfo: CurrencyInfo)

    @Query("SELECT * FROM CurrencyInfo ORDER BY code")
    suspend fun getCurrenciesList(): List<CurrencyInfo>

    @Query("SELECT * FROM CurrencyInfo WHERE is_selected = '1' ORDER BY code")
    suspend fun getShowCurrenciesList(): List<CurrencyInfo>

}