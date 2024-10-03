package com.app.currencyconverter.ui.selectcurrencies

import androidx.annotation.StringRes
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currencyconverter.R
import com.app.currencyconverter.data.models.CurrencyInfo
import com.app.currencyconverter.data.models.CurrencyToShow
import com.app.currencyconverter.data.repository.CurrencyRepository
import com.app.currencyconverter.utils.Constants.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("OPT_IN_USAGE", "SameParameterValue")
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: CurrencyRepository,
) : ViewModel() {

    private var currencyListMain: List<CurrencyToShow> = emptyList()

    private val selectionMap = HashMap<String, Boolean?>()

    private val _searchQuery = MutableStateFlow(EMPTY_STRING)
    val searchQuery = _searchQuery.asStateFlow()

    private val _baseCurrency = MutableStateFlow<String?>(null)
    val baseCurrency = _baseCurrency.asStateFlow()

    private val _updateCurrencyList = MutableStateFlow(false)
    val updateCurrencyList = _updateCurrencyList.asStateFlow()

    var currenciesListState = mutableStateListOf<CurrencyToShow>()

    private val _canPopBack = mutableStateOf(false)
    val canPopBack: State<Boolean> = _canPopBack

    private var canInitiateQuery = false

    private var canUpdateDB = false

    private val _snackBarMessage = mutableStateOf<Int?>(null)
    val snackBarMessage: State<Int?> = _snackBarMessage

    private fun showSnackBar(@StringRes message: Int) {
        viewModelScope.launch {
            _snackBarMessage.value = message
        }
    }

    fun updateDataFromDb() {
        canUpdateDB = false
        selectionMap.clear()
        viewModelScope.launch {

            currencyListMain = repository.getCurrencyList().map {
                if (it.isSelected) {
                    selectionMap[it.code] = true
                }
                CurrencyToShow(
                    code = it.code, countryName = it.countryName, isSelected = it.isSelected
                )
            }

            updateList(currencyListMain)

            canInitiateQuery = true

            searchQuery.debounce(600).distinctUntilChanged().filter { canInitiateQuery }
                .flatMapLatest { query ->
                    flow {
                        emit(searchCurrencies(query))
                    }
                }.collect { results ->
                    updateList(results)
                }

        }
    }

    private fun searchCurrencies(text: String): List<CurrencyToShow> {
        return currencyListMain.mapNotNull {
            if (it.code.contains(text, true) || it.countryName.contains(text, true)) it.apply {
                isSelected = selectionMap[it.code] == true
            }
            else {
                null
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onBaseCurrencyChanged(currency: String) {
        repository.setBaseCurrency(currency)
        _baseCurrency.value = currency
    }

    fun updateSelection(value: Boolean, index: Int? = null) {
        viewModelScope.launch {
            canUpdateDB = true
            if (index == null) {
                if (value) {
                    currencyListMain.forEach {
                        selectionMap[it.code] = true
                    }
                } else {
                    selectionMap.clear()
                }
                updateList(searchCurrencies(EMPTY_STRING))
            } else {
                val newList = currenciesListState.mapIndexed { i, item ->
                    if (i == index) {
                        if (value) selectionMap[item.code] = true
                        else selectionMap.remove(item.code)
                        //if (value) selectionCount++ else selectionCount--
                        item.copy(isSelected = value)
                    } else {
                        item
                    }
                }
                updateList(newList)
            }
        }
    }

    private fun updateList(newList: List<CurrencyToShow>) {
        currenciesListState.clear()
        currenciesListState.addAll(newList)
    }

    fun updateDB() {
        viewModelScope.launch {
            if (selectionMap.size == 0) showSnackBar(R.string.please_select_one)
            else {
                if (canUpdateDB) {
                    currencyListMain.forEach { item ->
                        repository.updateCurrencyInfo(
                            CurrencyInfo(
                                item.code, item.countryName, selectionMap[item.code] == true
                            )
                        )
                    }
                }
                _canPopBack.value = true
                _updateCurrencyList.value = true
            }
        }
    }

    fun snackBarShown() {
        _snackBarMessage.value = null
    }

    fun poppedBack() {
        _canPopBack.value = false
    }

    fun currencyListUpdated() {
        _updateCurrencyList.value = false
    }

    fun currencyUpdated() {
        _baseCurrency.value = null
    }
}