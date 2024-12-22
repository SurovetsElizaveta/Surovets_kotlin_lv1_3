package com.example.surovets_kotlin_lv1_3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    catalog: MutableState<Catalog?>,
    upperRowList: MutableState<UpperRowPhotos?>,
    cartList: MutableList<Pair<Product, Int>?>,
    totalSum: MutableState<Int>
) {
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isMain by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        isLoading = true
        delay(2000)
        isLoading = false
        isMain = true
    }
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(ContextCompat.getColor(context, R.color.bg_main)))
            ) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.lamp_1),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.couch_2),
                        contentDescription = null,
                    )
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.lamp_1),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        isMain -> {
            MainPageScreen(catalog, upperRowList, cartList, totalSum)
        }
    }
}
