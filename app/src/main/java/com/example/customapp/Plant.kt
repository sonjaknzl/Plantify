package com.example.customapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant (
    val name: String,
    val species: Int,
    val purchaseDate: String,
    val waterDate: String,
    var visibility: Boolean = false
): Parcelable