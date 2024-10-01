package com.app.currencyconverter.ui.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.currencyconverter.ui.selectcurrencies.SelectCurrencies
import com.app.currencyconverter.ui.selectcurrencies.SharedViewModel
import com.app.currencyconverter.utils.formatAmountWithCommas
import com.app.currencyconverter.utils.getColors
import kotlinx.serialization.Serializable


@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    mainViewModel: ConverterViewmodel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    navController: NavController = rememberNavController()
) {

    val state = mainViewModel.converterUiState.value
    val currencyList by mainViewModel.convertedCurrenciesList.collectAsState()
    val baseCurrency by sharedViewModel.baseCurrency.collectAsState()
    val amount by mainViewModel.amountText.collectAsState()
    var itemWidth by remember { mutableStateOf(0.dp) }

    if (baseCurrency.isNotBlank()) {
        mainViewModel.baseCurrency = baseCurrency
        mainViewModel.updateAmount()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {

            Box {
                TextField(
                    value = amount,
                    onValueChange = {
                        if (it.contains(" ") || it.contains("-"))
                            return@TextField
                        if (it.isNotBlank() && it.toDouble() != 0.0)
                            mainViewModel.updateAmount(it)
                        else mainViewModel.updateAmount("")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = { Text(text = "Enter amount here") }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                        .clickable(
                            indication = ripple(radius = 30.dp),
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate(SelectCurrencies())
                        }
                ) {
                    Text(
                        text = baseCurrency,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 15.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(offset = Offset(1f, 1f), blurRadius = 3.5f),
                            color = getColors().primary
                        ),
                    )
                }
            }


            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(currencyList, key = { _, info ->
                    info.code + info.countryName
                }) { index, info ->
                    Card(
                        modifier = Modifier
                            .padding(5.dp)
                            .aspectRatio(1f)
                            .onSizeChanged { size ->
                                if (index == 0) {
                                    itemWidth = size.width.dp
                                }
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(getColors().background)
                        ) {
                            Text(
                                text = info.code,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 4.dp),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold
                                ),
                            )
                            Text(
                                text = info.convertedValue.formatAmountWithCommas(),
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .padding(horizontal = 5.dp),
                                maxLines = 2,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    navController.navigate(SelectCurrencies(true))
                }
            ) {
                Text("Select custom currencies")
            }
        }
        if (state is ConverterViewmodel.ConverterUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

    }
}

@Serializable
object Converter