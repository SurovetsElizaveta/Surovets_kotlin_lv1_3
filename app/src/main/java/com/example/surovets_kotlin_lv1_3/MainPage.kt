package com.example.surovets_kotlin_lv1_3

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainPageScreen(
    catalog: MutableState<Catalog?>,
    upperRowList: MutableState<UpperRowPhotos?>,
    cartList: MutableList<Pair<Product, Int>?>,
    totalSum: MutableState<Int>
) {
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isFailed by rememberSaveable { mutableStateOf(false) }
    var isCart by rememberSaveable { mutableStateOf(false) }
    var isMain by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val message = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val handler = CoroutineExceptionHandler { _, exception ->
        run {
            isLoading = false
            isFailed = true
            isCart = false
            isMain = true
        }
    }
    LaunchedEffect(Unit) {
        isLoading = true
        ShowCatalog(
            context,
            coroutineScope,
            handler,
            catalog,
            upperRowList,
            { isLoading = it },
            { isFailed = it }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(ContextCompat.getColor(context, R.color.bg_main)))
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Color(ContextCompat.getColor(context, R.color.dark_orange))
                    )
                }
            }

            isFailed -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column() {
                        Box(modifier = Modifier.clickable(onClick = {
                            ShowCatalog(
                                context,
                                coroutineScope,
                                handler,
                                catalog,
                                upperRowList,
                                { isLoading = it },
                                { isFailed = it }
                            )
                        })) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            }

            isCart -> {
                CartScreen(catalog, upperRowList, cartList, totalSum)
            }

            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    UpperRow(upperRowList)
                    SearchBar(context, message)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        catalog.value?.catalog?.let {
                            items(it.size) {
                                OneProductCard(
                                    context,
                                    catalog.value!!.catalog[it],
                                    cartList,
                                    totalSum
                                )
                            }
                        }
                    }
                }
            }
        }
        MainBottomBar(context, { isCart = it }, { isMain = it })
    }
}


@Composable
fun UpperRow(
    upperRowList: MutableState<UpperRowPhotos?>
) {
    val listState = rememberLazyListState()
    var currentIndex = 0
    LaunchedEffect(upperRowList.value) {
        while (true) {
            delay(4000)
            val itemCount = upperRowList.value?.upperRow?.size ?: 0
            if (currentIndex < itemCount - 1) {
                listState.animateScrollToItem(currentIndex + 1)
                currentIndex = currentIndex + 1
            } else {
                listState.animateScrollToItem(0)
                currentIndex = 0
            }
        }
    }
    LazyRow(
        modifier = Modifier
            .height(200.dp)
            .padding(vertical = 8.dp),
        state = listState
    ) {
        upperRowList.value?.upperRow?.let {
            items(it.size) {
                Image(
                    painter = rememberAsyncImagePainter(upperRowList.value!!.upperRow[it].photo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .fillMaxHeight()
                        .width(352.dp),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}


@Composable
fun SearchBar(context: Context, message: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(ContextCompat.getColor(context, R.color.bg_dark)))
            .border(
                width = 1.dp,
                color = Color(ContextCompat.getColor(context, R.color.dark_orange)),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {},
        horizontalArrangement = Arrangement.End,
    ) {
        TextField(
            value = message.value,
            onValueChange = { message.value = it },
            textStyle = TextStyle(fontSize = 12.sp),
            modifier = Modifier
                .padding(vertical = 0.dp)
                .weight(6F),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(
                    ContextCompat.getColor(context, R.color.bg_dark)
                ),
                unfocusedContainerColor = Color(
                    ContextCompat.getColor(context, R.color.bg_dark)
                ),
                unfocusedIndicatorColor = Color(
                    ContextCompat.getColor(context, R.color.bg_dark)
                ),
                focusedIndicatorColor = Color(
                    ContextCompat.getColor(context, R.color.font_light_orange)
                )
            ),
            placeholder = {
                Text(
                    text = context.getString(R.string.search_placeholder),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(onDone = {})  // ONDONE ACTION (SEARCH)
        )
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.filter),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 10.dp)
                .clickable {}   // FILTER
                .weight(1f)
        )
        Button(
            onClick = {},   // SEARCH BUTTON
            modifier = Modifier
                .height(28.dp)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
                .weight(2f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(
                    ContextCompat.getColor(
                        context,
                        R.color.light_orange
                    )
                )
            ),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.search),
                contentDescription = null,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
            )
        }
    }
}


@Composable
fun OneProductCard(
    context: Context,
    product: Product,
    cartList: MutableList<Pair<Product, Int>?>,
    totalSum: MutableState<Int>
) {
    Column(  // TO ONE PRODUCT PAGE
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Color(
                    ContextCompat.getColor(
                        context,
                        R.color.bg_card
                    )
                )
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.photo),
            contentDescription = null,
            modifier = Modifier
                .height(128.dp)
                .padding(12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = product.name,
            fontSize = 12.sp,
        )
        Text(
            text = product.price.toString() + context.getString(R.string.rubles),
            fontSize = 16.sp,
            color = Color(
                ContextCompat.getColor(
                    context,
                    R.color.dark_orange
                )
            ),
            fontWeight = FontWeight(500),
            modifier = Modifier.padding(4.dp)
        )
        val buttonColor = remember {
            mutableStateOf(
                Color(
                    ContextCompat.getColor(
                        context,
                        R.color.light_orange
                    )
                )
            )
        }
        val buttonContentColor = remember {
            mutableStateOf(
                Color(
                    ContextCompat.getColor(
                        context,
                        R.color.bg_button_font_light
                    )
                )
            )
        }
        Button(
            onClick = {
                buttonColor.value =
                    Color(ContextCompat.getColor(context, R.color.bg_button_font_light))
                buttonContentColor.value =
                    Color(ContextCompat.getColor(context, R.color.light_orange))

                if (product.toString() in cartList.toString() == false) {
                    val newCart = Pair(product, 1)
                    cartList.add(newCart)
                    UpdateTotalSum(totalSum, cartList)
                }
                // BUY BUTTON
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor.value,
                contentColor = buttonContentColor.value
            ),
            border = BorderStroke(
                1.dp,
                color = Color(ContextCompat.getColor(context, R.color.light_orange))
            ),
            modifier = Modifier
                .padding(top = 4.dp, bottom = 12.dp)
                .height(32.dp),
        ) {
            Text(
                text = context.getString(R.string.buy_button_text),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                fontWeight = FontWeight(500),
            )
        }
    }
}

@Composable
fun MainBottomBar(
    context: Context,
    setCart: (Boolean) -> Unit,
    setMain: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color(ContextCompat.getColor(context, R.color.bg_main)))
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(90.dp)
                .clickable {
                    setMain(true)
                } // SWITCH TO MAIN PAGE
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.home),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(90.dp)
                .clickable {
                    setCart(true)
                } // SWITCH TO CART
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.cart_1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(90.dp)
                .clickable { } // SWITCH TO SIGH UP
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.exit_1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}


fun ShowCatalog(
    context: Context,
    coroutineScope: CoroutineScope,
    handler: CoroutineExceptionHandler,
    setCatalog: MutableState<Catalog?>,
    setUpperRow: MutableState<UpperRowPhotos?>,
    setLoading: (Boolean) -> Unit,
    setFailed: (Boolean) -> Unit
) {
    coroutineScope.launch(handler) {
        setLoading(true)
        setFailed(false)
        delay(2000)
        try {
            setCatalog.value = ReadJsonFileCatalog(context, "catalog_data.json")
            setUpperRow.value = ReadJsonFileRow(context, "upper_row_data.json")
            setLoading(false)
            setFailed(false)
        } catch (e: Exception) {
            setFailed(true)
        } finally {
            setLoading(false)
        }
    }
}

