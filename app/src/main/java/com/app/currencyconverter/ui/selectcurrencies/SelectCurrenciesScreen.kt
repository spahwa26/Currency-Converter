package com.app.currencyconverter.ui.selectcurrencies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.currencyconverter.utils.getColors
import kotlinx.serialization.Serializable


@Composable
fun SelectCurrenciesScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    args: SelectCurrencies,
    navController: NavController
) {

    val currencyList by sharedViewModel.currenciesList.collectAsState()
    val searchQuery by sharedViewModel.searchQuery.collectAsState()
    var itemWidth by remember { mutableStateOf(0.dp) }
    Box(contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
                .fillMaxSize()
        ) {

            TextField(
                value = searchQuery,
                onValueChange = { sharedViewModel.onSearchQueryChanged(it) },
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
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                placeholder = { Text(text = "Search currency here") }
            )


            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier.fillMaxSize()
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(getColors().background)
                                .clickable(
                                    indication = ripple(radius = 30.dp),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    sharedViewModel.onBaseCurrencyChanged(info.code)
                                    navController.popBackStack()
                                }
                        ) {
                            Text(
                                text = info.code,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .align(Alignment.Center),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold
                                ),
                            )
                            if (args.isMultipleSelection) Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 15.dp, end = 15.dp)
                            ) { // Set desired size
                                Checkbox(
                                    checked = false,
                                    onCheckedChange = { },
                                    modifier = Modifier.size(20.dp) // Reduce size of checkbox
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Serializable
data class SelectCurrencies(val isMultipleSelection: Boolean = false)