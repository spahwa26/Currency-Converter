package com.app.currencyconverter.ui.converter

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currencyconverter.R
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.models.CurrencyToShow
import com.app.currencyconverter.data.repository.CurrencyRepository
import com.app.currencyconverter.utils.Constants.AMOUNT_REGEX
import com.app.currencyconverter.utils.Constants.EMPTY_STRING
import com.app.currencyconverter.utils.NoInternetException
import com.app.currencyconverter.utils.ResultWrapper
import com.app.currencyconverter.utils.SomethingWentWrongException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConverterViewmodel @Inject constructor(
    private val repository: CurrencyRepository,
) :
    ViewModel() {


    private val regex = Regex(AMOUNT_REGEX)

    private val _converterUiState: MutableState<ConverterUiState?> = mutableStateOf(null)
    val converterUiState: State<ConverterUiState?> = _converterUiState

    private val _amountText = MutableStateFlow(EMPTY_STRING)
    val amountText = _amountText.asStateFlow()

    var baseCurrency: String = repository.getBaseCurrency()

    private var currencyList: List<CurrencyInfo>? = null

    private var currencyRates: CurrenciesData? = null

    val convertedCurrenciesList = MutableStateFlow<List<CurrencyToShow>>(emptyList())



    fun updateLists(callAPI: Boolean = false) {
        viewModelScope.launch {
                currencyList = repository.getShowCurrenciesList()
                currencyRates = repository.getCurrencyRates()
                if ((currencyList.isNullOrEmpty() || currencyRates == null) && callAPI) {
                    callConverterAPI()
                }
                else {
                    updateAmount(EMPTY_STRING)
                }
        }
    }

    private fun callConverterAPI() {
        _converterUiState.value = ConverterUiState.Loading
        viewModelScope.launch {
            when (val res = repository.updateCurrencyData()) {
                is ResultWrapper.Success -> {
                    _converterUiState.value = ConverterUiState.Success
                    updateLists()
                }

                is ResultWrapper.Error -> {
                    _converterUiState.value =
                        when (res.exception) {
                            is NoInternetException -> ConverterUiState.Error(customError = R.string.no_internet_error)
                            is SomethingWentWrongException -> ConverterUiState.Error(customError = R.string.something_went_wrong)
                            else -> ConverterUiState.Error(
                                res.exception.localizedMessage ?: EMPTY_STRING
                            )
                        }

                }
            }
        }
    }

    fun updateAmount(sAmount: String = amountText.value) {
        _amountText.value = sAmount
        if (!currencyList.isNullOrEmpty() && currencyRates != null) {
            val amount = (sAmount.ifBlank { "0" }).toFloat()
            val convertedList = mutableListOf<CurrencyToShow>()
            currencyList?.map {
                val rateMap = currencyRates?.rates
                val convertedAmount: Double =
                    ((amount / (rateMap?.get(baseCurrency)
                        ?: 0f)) * (rateMap?.get(it.code)
                        ?: 0f)).toDouble()
                convertedList.add(
                    CurrencyToShow(
                        it.code,
                        it.countryName,
                        rateMap?.get(it.code) ?: 0f,
                        convertedAmount
                    )
                )
            }
            convertedCurrenciesList.value = convertedList
        }
    }


    fun hideAlert() {
        _converterUiState.value = ConverterUiState.Nothing
    }

    // Allowing blank and . value to pass through because, on blank value the conversion is returning 0 which is required
    fun isValidAmount(input: String): Boolean {
        return (regex.matches(input) && input.length<=12) || input.isBlank()
    }


    sealed class ConverterUiState {
        data object Loading : ConverterUiState()
        data object Success : ConverterUiState()
        data class Error(val apiError: String? = null, @StringRes val customError: Int = R.string.something_went_wrong) :
            ConverterUiState()

        data object Nothing : ConverterUiState()
    }
}