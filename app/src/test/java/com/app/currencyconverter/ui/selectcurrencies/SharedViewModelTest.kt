package com.app.currencyconverter.ui.selectcurrencies

import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.repository.FakeRepository
import com.app.shared_test_code.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest{
    private lateinit var viewmodel: SharedViewModel

    private lateinit var repository: FakeRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        repository = FakeRepository()
        val curr1 = CurrencyInfo(code = "INR", countryName = "Indian Rupee", isSelected = false)
        val curr2 = CurrencyInfo(code = "USD", countryName = "Dollar", isSelected = false)
        val curr3 =
            CurrencyInfo(code = "AUD", countryName = "Australian Dollar", isSelected = false)
        val curr4 = CurrencyInfo(code = "AED", countryName = "Dinar", isSelected = true)

        repository.addCurrencies(curr1, curr2, curr3, curr4)

        viewmodel = SharedViewModel(repository)
    }

    @Test
    fun testOnOpen_checkIfDataIsAvailableIn()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        advanceUntilIdle()

        assertThat(viewmodel.currenciesListState.size).isEqualTo(4)
    }

    @Test
    fun testSearchByCountryName()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.onSearchQueryChanged("Dollar")

        advanceUntilIdle()

        assertThat(viewmodel.currenciesListState.size).isEqualTo(2)
    }

    @Test
    fun testSearchByCountryCode()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.onSearchQueryChanged("AUD")

        advanceUntilIdle()

        assertThat(viewmodel.currenciesListState.size).isEqualTo(1)
    }

    @Test
    fun testUpdateItemSelectionOnFirstItem()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.updateSelection(true, 0)

        advanceUntilIdle()

        assertThat(viewmodel.currenciesListState[0].isSelected).isTrue()
    }

    @Test
    fun testUpdateItemSelectionOfAllItems()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.updateSelection(true, null)

        advanceUntilIdle()

        var allItemsSelected = true

        viewmodel.currenciesListState.forEach {
            if(!it.isSelected) {
                allItemsSelected = false
                return@forEach
            }
        }

        assertThat(allItemsSelected).isTrue()
    }

    @Test
    fun testUpdateItemUnSelectionOfAllItems()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.updateSelection(false, null)

        advanceUntilIdle()

        var noItemsSelected = true

        viewmodel.currenciesListState.forEach {
            if(it.isSelected) {
                noItemsSelected = false
                return@forEach
            }
        }

        assertThat(noItemsSelected).isTrue()
    }

    @Test
    fun checkIfBaseCurrencyUpdating(){

        assertThat(viewmodel.baseCurrency.value).isNotEqualTo("INR")

        viewmodel.onBaseCurrencyChanged("INR")

        assertThat(viewmodel.baseCurrency.value).isEqualTo("INR")

        viewmodel.currencyUpdated()

        assertThat(viewmodel.baseCurrency.value).isEqualTo(null)
    }

    @Test
    fun givenAllItemsAreUnselected_TestSnackBarIsTriggered()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.updateSelection(false, null)

        assertThat(viewmodel.snackBarMessage.value).isEqualTo(null)

        viewmodel.updateDB()

        advanceUntilIdle()

        assertThat(viewmodel.snackBarMessage.value).isNotEqualTo(null)

        viewmodel.snackBarShown()

        assertThat(viewmodel.snackBarMessage.value).isEqualTo(null)

    }

    @Test
    fun givenOneItemIsSelected_TestUpdateDbForPopBackAndListUpdate()= runTest{
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateDataFromDb()

        viewmodel.updateDB()

        advanceUntilIdle()

        assertThat(viewmodel.canPopBack.value).isTrue()

        assertThat(viewmodel.updateCurrencyList.value).isTrue()

        viewmodel.poppedBack()

        viewmodel.currencyListUpdated()

        assertThat(viewmodel.canPopBack.value).isFalse()

        assertThat(viewmodel.updateCurrencyList.value).isFalse()

    }


}