package com.example.surovets_kotlin_lv1_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var catalog = remember { mutableStateOf<Catalog?>(null) }
            var cartList = remember { mutableListOf<Pair<Product, Int>?>() }
            var upperRowList = remember { mutableStateOf<UpperRowPhotos?>(null) }
            var totalSum = remember { mutableStateOf<Int>(0) }

            StartScreen(catalog, upperRowList, cartList, totalSum)
        }
    }
}
