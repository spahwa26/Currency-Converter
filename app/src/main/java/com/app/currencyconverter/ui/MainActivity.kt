package com.app.currencyconverter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.app.currencyconverter.ui.converter.Converter
import com.app.currencyconverter.ui.converter.ConverterScreen
import com.app.currencyconverter.ui.selectcurrencies.SelectCurrencies
import com.app.currencyconverter.ui.selectcurrencies.SelectCurrenciesScreen
import com.app.currencyconverter.ui.selectcurrencies.SharedViewModel
import com.app.currencyconverter.ui.theme.CurrencyConverterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                val viewmodel: SharedViewModel = hiltViewModel()
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Converter
                    ) {
                        composable<Converter> {
                            ConverterScreen(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                                sharedViewModel = viewmodel
                            )
                        }
                        composable<SelectCurrencies> {backStackEntry ->
                            val selectCurrencies : SelectCurrencies = backStackEntry.toRoute()
                            SelectCurrenciesScreen(
                                modifier = Modifier.padding(innerPadding),
                                sharedViewModel = viewmodel,
                                navController = navController,
                                args = selectCurrencies
                            )
                        }
                    }

                }
            }
        }
    }
}