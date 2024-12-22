package com.example.surovets_kotlin_lv1_3

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun CartScreen(
    catalog: MutableState<Catalog?>,
    upperRowList: MutableState<UpperRowPhotos?>,
    cartList: MutableList<Pair<Product, Int>?>,
    totalSum: MutableState<Int>
) {
    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isFailed by rememberSaveable { mutableStateOf(false) }
    var isCart by rememberSaveable { mutableStateOf(true) }
    var isMain by rememberSaveable { mutableStateOf(false) }
    var isRefresh by rememberSaveable { mutableStateOf(false) }
    var isEmpty by rememberSaveable { mutableStateOf(false) }
    if (cartList.isEmpty()) {
        isEmpty = true
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
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
                        isRefresh = true
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

        isMain -> {
            MainPageScreen(catalog, upperRowList, cartList, totalSum)
        }
        isEmpty -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(ContextCompat.getColor(context, R.color.bg_main))),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CartHeader(context)
                Column( modifier = Modifier
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "В корзине пока пусто :(",
                        fontSize = 20.sp,
                        color = Color(ContextCompat.getColor(context, R.color.light_gray)),
                    )
                    Text(
                        text = "Перейти к каталогу",
                        fontSize = 20.sp,
                        color = Color(ContextCompat.getColor(context, R.color.light_orange)),
                        modifier = Modifier
                            .clickable {
                                isMain = true
                            },
                        textDecoration = TextDecoration.Underline
                    )
                }
                CartBottomBar(context, { isMain = it }, { isCart = it })
            }
        }

        isCart or isRefresh -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(ContextCompat.getColor(context, R.color.bg_main))),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CartHeader(context)
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartList) { pair ->
                        if (pair != null) {
                            CartOneProduct(
                                context,
                                pair,
                                cartList,
                                { isRefresh = it },
                                {isEmpty = it},
                                totalSum
                            )
                        }
                    }
                }
                CartFinalStep(context, totalSum)
                CartBottomBar(context, { isMain = it }, { isCart = it })
            }
            isRefresh = false
        }
    }
}


@Composable
fun CartOneProduct(
    context: Context,
    pair: Pair<Product, Int>,
    cartList: MutableList<Pair<Product, Int>?>,
    setRefresh: (Boolean) -> Unit,
    setEmpty: (Boolean) -> Unit,
    totalSum: MutableState<Int>
) {
    Row(
        modifier = Modifier
            .height(128.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(ContextCompat.getColor(context, R.color.bg_card)))
    ) {
        Image( // SWITCH TO ONE PRODUCT PAGE
            painter = rememberAsyncImagePainter(pair.first.photo),
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .weight(2f),
            contentScale = ContentScale.Crop

        )
        Column(
            modifier = Modifier
                .weight(2f)
                .padding(10.dp)
        ) {
            Text(
                text = pair.first.name,
                fontSize = 16.sp,
            )
            Text(
                text = context.getString(R.string.cart_product_size) + pair.first.dimensions.length +
                        context.getString(R.string.size_star) + pair.first.dimensions.width +
                        context.getString(R.string.size_star) + pair.first.dimensions.height + "\n" +
                        context.getString(R.string.cart_product_color) + " " + pair.first.color,
                fontSize = 12.sp
            )
            Text(
                text = (pair.first.price * pair.second).toString() + context.getString(R.string.rubles),
                fontSize = 16.sp,
                color = Color(ContextCompat.getColor(context, R.color.dark_orange)),
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight(500)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp, bottom = 8.dp)
                .align(Alignment.Bottom)
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color(ContextCompat.getColor(context, R.color.bg_dark))),
        ) {
            Text(
                text = context.getString(R.string.cart_minus),
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(2f)
                    .clickable {
                        setRefresh(true)
                        MinusProduct(cartList, pair)
                        UpdateTotalSum(totalSum, cartList)
                        if (cartList.isEmpty()) {
                            setEmpty(true)
                        }
                    },                                          // MINUS ONE OR DELETE
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(500),
                color = Color(ContextCompat.getColor(context, R.color.gray))
            )
            Text(
                text = pair.second.toString(),   //
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight(500),
                color = Color(ContextCompat.getColor(context, R.color.font_dark_beige)),
                textAlign = TextAlign.Center
            )
            Text(
                text = context.getString(R.string.cart_plus),
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(2f)
                    .clickable {
                        setRefresh(true)
                        PlusProduct(cartList, pair)
                        UpdateTotalSum(totalSum, cartList)
                    },                                         // PLUS ONE and change sum
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(500),
                color = Color(ContextCompat.getColor(context, R.color.gray))
            )
        }
    }
}

@Composable
fun CartHeader(context: Context) {
    Text(
        text = context.getString(R.string.cart_header),
        fontSize = 24.sp,
        color = Color(ContextCompat.getColor(context, R.color.dark_orange)),
        modifier = Modifier.padding(12.dp)
    )
}

@Composable
fun CartFinalStep(context: Context, totalSum: MutableState<Int>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(ContextCompat.getColor(context, R.color.bg_card)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = context.getString(R.string.cart_total),
                fontSize = 20.sp,
                color = Color(ContextCompat.getColor(context, R.color.dark_orange)),
                fontWeight = FontWeight(500),
                modifier = Modifier
            )
            Text(
                text = totalSum.value.toString() + " " + context.getString(R.string.rubles),
                fontSize = 20.sp,
            )
        }
        HorizontalDivider(
            color = Color(ContextCompat.getColor(context, R.color.light_gray)),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Row(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = context.getString(R.string.cart_delivery_header),
                color = Color(ContextCompat.getColor(context, R.color.light_gray)),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = context.getString(R.string.delivery_date),
                fontSize = 16.sp,
                color = Color(ContextCompat.getColor(context, R.color.gray))
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = context.getString(R.string.cart_pay_header),
                color = Color(ContextCompat.getColor(context, R.color.light_gray)),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = context.getString(R.string.payment_variant),
                fontSize = 16.sp,
                color = Color(ContextCompat.getColor(context, R.color.gray))
            )
        }
        Button(
            onClick = {}, // SWITCH TO FINAL PAGE
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(
                    ContextCompat.getColor(context, R.color.light_orange)
                )
            ), modifier = Modifier
                .padding(8.dp)
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = context.getString(R.string.cart_place_on_order_button_text),
                fontSize = 20.sp,
                color = Color(ContextCompat.getColor(context, R.color.bg_button_font_light))
            )
        }
    }
}

@Composable
fun CartBottomBar(
    context: Context, setMain: (Boolean) -> Unit, setCart: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color(ContextCompat.getColor(context, R.color.bg_main)))
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier
            .fillMaxHeight()
            .width(90.dp)
            .clickable {
                setMain(true)
            } // SWITCH TO MAIN PAGE
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.home),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .width(90.dp)
            .clickable {
                setCart(true)
            } // REFRESH CART
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.cart_1),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .width(90.dp)
            .clickable { } // SWITCH TO SIGH UP
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.exit_1),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

fun MinusProduct(cartList: MutableList<Pair<Product, Int>?>, pair: Pair<Product, Int>) {
    if (pair.second == 1) {
        cartList.remove(pair)
    } else {
        cartList.forEach { el_pair ->
            if (el_pair != null) {
                if (el_pair.first.hashCode() == pair.first.hashCode()) {
                    if (el_pair.first == pair.first) {
//                        Log.i("PAIR", cartList.toString())
                        val changedPair = Pair(el_pair.first, el_pair.second - 1)
                        val ind = cartList.indexOf(el_pair)
                        cartList[ind] = changedPair
                    }
                }
            }
        }
    }
}

fun PlusProduct(cartList: MutableList<Pair<Product, Int>?>, pair: Pair<Product, Int>) {
    cartList.forEach { el_pair ->
        if (el_pair != null) {
            if (el_pair.first.hashCode() == pair.first.hashCode()) {
                if (el_pair.first == pair.first) {
//                    Log.i("PAIR", cartList.toString())
                    val changedPair = Pair(el_pair.first, el_pair.second + 1)
                    val ind = cartList.indexOf(el_pair)
                    cartList[ind] = changedPair

                }
            }
        }
    }
}

fun UpdateTotalSum(totalSum: MutableState<Int>, cartList: MutableList<Pair<Product, Int>?>) {
    totalSum.value = 0
    cartList.forEach { pair ->
        if (pair != null) {
            totalSum.value = totalSum.value + (pair.first.price * pair.second)
        }
    }
}