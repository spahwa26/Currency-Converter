package com.app.currencyconverter.ui.converter

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
class ConverterViewmodelTest {

    private lateinit var viewmodel: ConverterViewmodel

    private lateinit var repository: FakeRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        repository = FakeRepository()

        viewmodel = ConverterViewmodel(repository)
    }

    @Test
    fun updateListsTest__checkApiIsNotCalling() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateLists(true)

        advanceUntilIdle()

        assertThat(viewmodel.converterUiState.value == null).isTrue()
    }

    @Test
    fun updateListsTest__checkApiIsCallingWithSuccessResult() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        repository.shouldCallApi()

        viewmodel.updateLists(true)

        advanceUntilIdle()

        assertThat(viewmodel.converterUiState.value).isEqualTo(ConverterViewmodel.ConverterUiState.Success)
    }

    @Test
    fun updateListsTest__checkApiIsCallingWithErrorResult() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        repository.shouldCallApi()

        repository.shouldShowError()

        viewmodel.updateLists(true)

        advanceUntilIdle()

        assertThat(viewmodel.converterUiState.value).isEqualTo(ConverterViewmodel.ConverterUiState.Error())
    }

    @Test
    fun updateAmountTest__checkCurrencyListIsNotEmpty() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateLists()

        advanceUntilIdle()

        assertThat(viewmodel.convertedCurrenciesList.value.isNotEmpty()).isTrue()
    }

    @Test
    fun updateAmountTest__checkCurrencyListItemAmountIsNotZero() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        viewmodel.updateLists()

        advanceUntilIdle()

        viewmodel.updateAmount("1")

        assertThat(viewmodel.convertedCurrenciesList.value[0].convertedValue > 0).isTrue()
    }

    @Test
    fun testAlertHiding_whenApiThrowsErrorThenCallHideAlert() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        repository.shouldCallApi()

        repository.shouldShowError()

        viewmodel.updateLists(true)

        advanceUntilIdle()

        assertThat(viewmodel.converterUiState.value).isEqualTo(ConverterViewmodel.ConverterUiState.Error())

        viewmodel.hideAlert()

        assertThat(viewmodel.converterUiState.value).isEqualTo(ConverterViewmodel.ConverterUiState.Nothing)

    }

    @Test
    fun givenVariousInvalidStrings_testAmountValidation() {
        //test for .. with value
        assertThat(viewmodel.isValidAmount("..")).isFalse()
        assertThat(viewmodel.isValidAmount("1..")).isFalse()
        assertThat(viewmodel.isValidAmount("..1")).isFalse()
        assertThat(viewmodel.isValidAmount("1.2.1")).isFalse()

        //test for alphanumeric value
        assertThat(viewmodel.isValidAmount("1ab")).isFalse()

        //test for special characters
        assertThat(viewmodel.isValidAmount("-")).isFalse()
        assertThat(viewmodel.isValidAmount("1-")).isFalse()
        assertThat(viewmodel.isValidAmount("2-1")).isFalse()
        assertThat(viewmodel.isValidAmount("2 1")).isFalse()
        assertThat(viewmodel.isValidAmount("2-1")).isFalse()
        assertThat(viewmodel.isValidAmount("=")).isFalse()
        assertThat(viewmodel.isValidAmount("+")).isFalse()
        assertThat(viewmodel.isValidAmount("%")).isFalse()
        assertThat(viewmodel.isValidAmount("&")).isFalse()
        assertThat(viewmodel.isValidAmount("1&")).isFalse()
        assertThat(viewmodel.isValidAmount("&2")).isFalse()

        // test for number with more than 12 length
        assertThat(viewmodel.isValidAmount("1111111111111")).isFalse()

    }

    @Test
    fun givenVariousValidStrings_testAmountValidation() {
        assertThat(viewmodel.isValidAmount("0")).isTrue()
        assertThat(viewmodel.isValidAmount("123")).isTrue()
        assertThat(viewmodel.isValidAmount("1000")).isTrue()
        assertThat(viewmodel.isValidAmount(".1")).isTrue()
        assertThat(viewmodel.isValidAmount("0.1")).isTrue()
        assertThat(viewmodel.isValidAmount("1.0")).isTrue()
        assertThat(viewmodel.isValidAmount("12.23")).isTrue()
        assertThat(viewmodel.isValidAmount("1000.00")).isTrue()
        assertThat(viewmodel.isValidAmount("999999999999")).isTrue()
    }


}