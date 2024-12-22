package com.example.surovets_kotlin_lv1_3

import android.content.Context
import com.google.gson.Gson
import java.io.IOException

fun ReadJsonFileCatalog(context : Context, filename : String) : Catalog? {
    val jsonString: String
    try {
        jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
        val gson = Gson()
        val catalog : Catalog = gson.fromJson(jsonString, Catalog::class.java)
        return catalog

    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
}

fun ReadJsonFileRow(context : Context, filename : String) : UpperRowPhotos? {
    val jsonString: String
    try {
        jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
        val gson = Gson()
        val upperRow : UpperRowPhotos = gson.fromJson(jsonString, UpperRowPhotos::class.java)
        return upperRow

    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
}
