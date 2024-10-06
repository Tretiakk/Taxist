package com.taxi.taxist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap

data class DriverInfo(
    val name: String,
    val tariffH: Int,
    val tariffM: Int,
    val tariffS: Int,
    val rate: Float,
    val car: String,
    val typeOfCar: String,
    val experience: Int,
    val phoneNumber: String,
    val image: ImageBitmap?,
    var isExpended: MutableState<Boolean> = mutableStateOf(false),
)
