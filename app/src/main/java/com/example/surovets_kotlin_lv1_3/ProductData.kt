package com.example.surovets_kotlin_lv1_3

data class Dimension(
    val length: String,
    val width: String,
    val height: String
)

data class Product(
    val category: String,
    val name: String,
    val price: Int,
    val photo: String,
    val description: String,
    val material: String,
    val color: String,
    val dimensions: Dimension
)

data class Catalog(
    val upperRowPhotos: List<UpperRowPhoto>,
    val catalog: List<Product>
)

data class UpperRowPhoto(
    val photo : String
)

data class UpperRowPhotos(
    val upperRow : MutableList<UpperRowPhoto>
)