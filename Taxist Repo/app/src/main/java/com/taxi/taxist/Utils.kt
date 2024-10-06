package com.taxi.taxist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object Utils {

    fun message(
        shortDescription: String,
        description: String,
        buttonDescription: String,
        onButtonClick: () -> Unit = {}
    ) {
        messageShortDes.value = shortDescription
        messageButtonDes.value = buttonDescription
        messageDes.value = description
        messageOnOk.value = onButtonClick

        isMessageVisible.value = true
    }

    internal fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
        }
        return false
    }

    // Function that converts the vector form to Bitmap form.
    internal fun bitmapDescriptorFromVector(context: android.content.Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        return vectorDrawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.draw(canvas)
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}
