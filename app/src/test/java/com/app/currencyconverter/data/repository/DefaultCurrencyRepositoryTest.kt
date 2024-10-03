package com.app.currencyconverter.data.repository

import com.app.currencyconverter.data.localdb.CurrenciesDatabase
import com.app.currencyconverter.data.localdb.dao.CurrencyDao
import com.app.currencyconverter.data.localdb.preferences.CurrencyPreferences
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.network.CurrencyApi
import com.app.currencyconverter.utils.ResultWrapper
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyList
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.Response

@ExperimentalCoroutinesApi
class DefaultCurrencyRepositoryTest {

    @Mock
    private lateinit var mockClient: CurrencyApi

    @Mock
    private lateinit var mockDatabase: CurrenciesDatabase

    @Mock
    private lateinit var mockPrefs: CurrencyPreferences

    @Mock
    private lateinit var mockCurrencyDao: CurrencyDao

    private lateinit var repository: DefaultCurrencyRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockDatabase.currencyDao()).thenReturn(mockCurrencyDao)
        repository = DefaultCurrencyRepository(mockClient, mockDatabase, mockPrefs)
    }

    @Test
    fun updateCurrencyData_CheckSuccessWithCurrencyNames() = runTest {
        val mockRates = CurrenciesData(
            0,
            "USD",
            "Disclaimer",
            "License",
            1,
            hashMapOf("EUR" to 0.85f),
            1234567890
        )
        val mockInfo = hashMapOf("USD" to "United States Dollar", "EUR" to "Euro")

        `when`(mockClient.getCurrencyData()).thenReturn(Response.success(mockRates))
        `when`(mockClient.getCurrencyInfo()).thenReturn(Response.success(mockInfo))

        val result = repository.updateCurrencyData(true)

        assertThat(result is ResultWrapper.Success).isTrue()
        verify(mockCurrencyDao).insertCurrenciesRates(mockRates)
        verify(mockCurrencyDao).insertCurrenciesInfo(
            listOf(
                CurrencyInfo("USD", "United States Dollar"),
                CurrencyInfo("EUR", "Euro")
            )
        )
    }

    @Test
    fun updateCurrencyData_CheckSuccessWithoutCurrencyNames() = runTest {
        val mockRates = CurrenciesData(
            0,
            "USD",
            "Disclaimer",
            "License",
            1,
            hashMapOf("EUR" to 0.85f),
            1234567890
        )

        `when`(mockClient.getCurrencyData()).thenReturn(Response.success(mockRates))

        val result = repository.updateCurrencyData(false)

        assertThat(result is ResultWrapper.Success).isTrue()
        verify(mockCurrencyDao).insertCurrenciesRates(mockRates)
        verify(mockCurrencyDao, never()).insertCurrenciesInfo(anyList())
    }

    @Test
    fun updateCurrencyData_CheckFailureInGetCurrencyData() = runTest {
        val errorResponse: Response<CurrenciesData> = Response.error(400, "".toResponseBody(null))
        `when`(mockClient.getCurrencyData()).thenReturn(errorResponse)

        val result = repository.updateCurrencyData(true)

        assertThat(result).isInstanceOf(ResultWrapper.Error::class.java)
        verify(mockCurrencyDao, never()).insertCurrenciesRates(any())
        verify(mockCurrencyDao, never()).insertCurrenciesInfo(anyList())
    }

    @Test
    fun getCurrencyList_delegatesToDao() = runTest {
        repository.getCurrencyList()
        verify(mockCurrencyDao).getCurrenciesList()
    }

    @Test
    fun getShowCurrenciesList_delegatesToDao() = runTest {
        repository.getShowCurrenciesList()
        verify(mockCurrencyDao).getShowCurrenciesList()
    }

    @Test
    fun getCurrencyRates_delegatesToDao() = runTest {
        repository.getCurrencyRates()
        verify(mockCurrencyDao).getCurrencyRates()
    }

    @Test
    fun getUpdatesCount_delegatesToDao() = runTest {
        repository.getUpdatesCount()
        verify(mockCurrencyDao).getUpdatesCount()
    }

    @Test
    fun updateTheCount_delegatesToDao() = runTest {
        repository.updateTheCount(5)
        verify(mockCurrencyDao).updateCountInRatesTable(5)
    }

    @Test
    fun updateCurrencyInfo_delegatesToDao() = runTest {
        val info = CurrencyInfo("USD", "United States Dollar")
        repository.updateCurrencyInfo(info)
        verify(mockCurrencyDao).updateCurrenciesInfo(info)
    }

    @Test
    fun getBaseCurrencyDelegatesToPrefs() {
        `when`(mockPrefs.baseCurrency).thenReturn("USD")
        assertThat("USD").isEqualTo(repository.getBaseCurrency())
    }

    @Test
    fun setBaseCurrency_delegatesToPrefs() {
        repository.setBaseCurrency("EUR")
        verify(mockPrefs).baseCurrency = "EUR"
    }
}