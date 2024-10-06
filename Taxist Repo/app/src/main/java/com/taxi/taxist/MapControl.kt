package com.taxi.taxist

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

class MapControl {

    private var lastFrom: LatLng? = null
    private var lastTo: LatLng? = null

    // Directions
    fun getPolyline(result: DirectionsResult): MutableList<LatLng>? {
        for (route in result.routes) {
            val decodedPath = PolylineEncoding.decode(route.overviewPolyline.encodedPath)

            val newDecodedPath: MutableList<LatLng> = ArrayList()

            // This loops through all the LatLng coordinates of ONE polyline.
            for (latLng in decodedPath) {
                newDecodedPath.add(
                    LatLng(
                        latLng.lat,
                        latLng.lng
                    )
                )
            }

            return newDecodedPath
        }
        return null
    }

    private fun getDirections(
        context: Context,
        from: LatLng, to: LatLng,
        onResult: (result: DirectionsResult) -> Unit,
        onFailure: () -> Unit
    ) {
        val destination = com.google.maps.model.LatLng(to.latitude, to.longitude)

        val geoApiContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.GOOGLE_MAP_KEY)
            .build()
        val directions = DirectionsApiRequest(geoApiContext)

        directions.alternatives(false)
        directions.mode(TravelMode.DRIVING)
        directions.origin(
            com.google.maps.model.LatLng(from.latitude, from.longitude)
        )
        // request to google maps
        directions.destination(destination)
            .setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    onResult(result)
                }

                override fun onFailure(e: Throwable) {
                    onFailure()
                }
            })
    }

    fun putRoad(
        context: Context,
        from: LatLng,
        to: LatLng,
        onResult: (result: DirectionsResult) -> Unit,
        onFailure: () -> Unit
    ) {
        if (from !== lastFrom || to !== lastTo) {
            getDirections(
                context,
                from,
                to,
                onResult,
                onFailure
            )

            lastFrom = from
            lastTo = to
        }
    }

    private fun getLastLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        // Task<Location> task = fusedLocationProviderClient.getLastLocation();
    }

    fun setDriver(driver: String?) {
        // you need to set that to the driver field
        //String.format("%s : %s", getString(R.string.driver), driver)
    }

}