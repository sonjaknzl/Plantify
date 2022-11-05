package com.example.customapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val name: String,
    val species: Int,
    val purchaseDate: String,
    var waterDate: String,
    var nextWaterDate: String,
    var infoText: String,
    var visibility: Boolean = false
) : Parcelable