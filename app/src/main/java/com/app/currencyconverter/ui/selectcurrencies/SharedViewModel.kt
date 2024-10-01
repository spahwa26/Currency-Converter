package com.app.currencyconverter.ui.selectcurrencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currencyconverter.data.models.CurrencyToShow
import com.app.currencyconverter.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class SharedViewModel @Inject constructor(
    private val localRepository: LocalRepository,
) :
    ViewModel() {

    private var currencyList: List<CurrencyToShow>? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _baseCurrency = MutableStateFlow(localRepository.getBaseCurrency())
    val baseCurrency = _baseCurrency.asStateFlow()

    private val _currenciesList = MutableStateFlow<List<CurrencyToShow>>(emptyList())
    val currenciesList: StateFlow<List<CurrencyToShow>> = _currenciesList.asStateFlow()

    init {
        getListFromDb()
    }

    private fun getListFromDb() {
        viewModelScope.launch {
            withContext(IO) {
                currencyList = localRepository.getCurrencyList().map {
                    CurrencyToShow(code = it.code, countryName = it.countryName)
                }
            }
            searchQuery
                .debounce(600)  // Only search for queries with 2 or more characters
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        emit(searchCurrencies(query))
                    }
                }
                .collect { results ->
                    _currenciesList.value = results
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onBaseCurrencyChanged(currency: String) {
        localRepository.setBaseCurrency(currency)
        _baseCurrency.value = currency
    }

    private fun searchCurrencies(text: String): List<CurrencyToShow> {
        return currencyList?.filter {
            it.code.contains(text, true) || it.countryName.contains(text, true)
        } ?: emptyList()
    }
}