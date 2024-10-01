package com.app.currencyconverter.ui.converter

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currencyconverter.data.models.CurrenciesData
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.models.CurrencyToShow
import com.app.currencyconverter.data.repository.LocalRepository
import com.app.currencyconverter.data.repository.RemoteRepository
import com.app.currencyconverter.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ConverterViewmodel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) :
    ViewModel() {

    private val _converterUiState: MutableState<ConverterUiState?> = mutableStateOf(null)
    val converterUiState: State<ConverterUiState?> = _converterUiState

    private val _amountText = MutableStateFlow("")
    val amountText = _amountText.asStateFlow()

    var baseCurrency: String = localRepository.getBaseCurrency()

    private var currencyList: List<CurrencyInfo>? = null

    private var currencyRates: CurrenciesData? = null

    val convertedCurrenciesList = MutableStateFlow<List<CurrencyToShow>>(emptyList())

    init {
        updateLists(true)
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

    private fun callConverterAPI() {
        _converterUiState.value = ConverterUiState.Loading
        viewModelScope.launch {
            when (val res = remoteRepository.updateCurrencyData()) {
                is ResultWrapper.Success -> {
                    _converterUiState.value = ConverterUiState.Success
                    updateLists()
                    //updateAmount("2000")
                }

                is ResultWrapper.Error -> {
                    _converterUiState.value =
                        ConverterUiState.Error(res.exception.localizedMessage)
                }
            }
        }
    }

    fun updateLists(callAPI: Boolean = false) {
        viewModelScope.launch {
            withContext(IO) {
                currencyList = localRepository.getShowCurrenciesList()
                currencyRates = localRepository.getCurrencyRates()
                if ((currencyList.isNullOrEmpty() || currencyRates == null) && callAPI)
                    callConverterAPI()
                else {
                    updateAmount("")
                }
            }
        }
    }

    fun validateAmount(input: String): Boolean {
        val regex = Regex("^\\d*\\.?\\d{0,2}$")
        return regex.matches(input) || input.isEmpty()
    }


    sealed class ConverterUiState {
        data object Loading : ConverterUiState()
        data object Success : ConverterUiState()
        data class Error(val e: String?) : ConverterUiState()
    }
}