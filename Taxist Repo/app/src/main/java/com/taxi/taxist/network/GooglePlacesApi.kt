package com.taxi.taxist.network

import android.content.Context
import com.taxi.taxist.BuildConfig
import com.taxi.taxist.Map
import com.taxi.taxist.Utils
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

class GooglePlacesApi {

    private val retrofit = Retrofit.Builder()
            .baseUrl(GooglePlacesService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val googlePlacesService = retrofit.create(GooglePlacesService::class.java)

    suspend fun makeSearchRequest(context: Context, text: String): List<Prediction> {
        Map.isNetworkConnected.value = Utils.isNetworkConnected(context)

        if (Map.isNetworkConnected.value) {
            val list = googlePlacesService.getPredictions(
                text,
                getSupportedCodeOfCountry(),
                BuildConfig.GOOGLE_MAP_KEY
            )
                .body()?.predictions ?: listOf()

            return list
        }
        return listOf()
    }

    suspend fun makeSearchRequest(context: Context, text: String, location: Location): List<Prediction> {
        Map.isNetworkConnected.value = Utils.isNetworkConnected(context)

        if (Map.isNetworkConnected.value) {

            val list = googlePlacesService.getPredictions(
                text,
                "circle:10000@${location.lat},${location.lng}",
                getSupportedCodeOfCountry(),
                BuildConfig.GOOGLE_MAP_KEY
            ).body()?.predictions ?: listOf()

            return list
        }
        return listOf()
    }

    suspend fun makePlaceRequest(context: Context, place: String): PlaceInfo? {
        Map.isNetworkConnected.value = Utils.isNetworkConnected(context)

        if (Map.isNetworkConnected.value) {
            return googlePlacesService.getPlaceInfo(place,"address_components,geometry",  BuildConfig.GOOGLE_MAP_KEY).body()!!
        }

        return null
    }

    fun getPlacesData(){

    }

    fun getPlaceInfo(){

    }


    private val supporterCodes = listOf("af", "ja", "sq", "kn", "am", "kk", "ar", "km", "hy", "ko","az", "ky", "eu", "lo", "be", "lv", "bn", "lt", "bs", "mk","bg", "ms", "my", "ml", "ca", "mr", "zh", "mn", "zh-CN", "ne","zh-HK", "no", "zh-TW", "pl", "hr", "pt", "cs", "pt-BR", "da", "pt-PT","nl", "pa", "en", "ro", "en-AU", "ru", "en-GB", "sr", "et", "si","fa", "sk", "fi", "sl", "fil", "es", "fr", "es-419", "fr-CA", "sw","gl", "sv", "ka", "ta", "de", "te", "el", "th", "gu", "tr","iw", "uk", "hi", "ur", "hu", "uz", "is", "vi", "id", "zu")
    private fun getSupportedCodeOfCountry(): String{
        val locale = Locale.getDefault().language

        if (supporterCodes.contains(locale)){
            return locale
        }
        return "en"
    }
}

internal interface GooglePlacesService {

    @GET("maps/api/place/autocomplete/json")
    suspend fun getPredictions(
        @Query("input") text: String,
        @Query("locationbias") location: String,
        @Query("language") language: String,
        @Query("key") apiKey: String,
    ): Response<Predictions>

    @GET("maps/api/place/autocomplete/json")
    suspend fun getPredictions(
        @Query("input") text: String,
        @Query("language") language: String,
        @Query("key") apiKey: String,
    ): Response<Predictions>

    @GET("maps/api/place/details/json")
    suspend fun getPlaceInfo(
        @Query("place_id") place: String,
        @Query("fields") fields: String,
        @Query("key") apiKey: String,
    ): Response<PlaceInfo>

    companion object{
        const val BASE_URL = "https://maps.googleapis.com/"
    }
}

data class Location(
    val lat: Double,
    val lng: Double
)

data class Geometry(
    val location: Location,
)

data class AddressComponents(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)

data class Result(
    val geometry: Geometry,
)

data class PlaceInfo(
    val address_components: List<AddressComponents>,
    val result: Result,
    val status: String
)

data class Predictions(
    val predictions: List<Prediction>
)

data class Prediction(
    val description: String,
    val matched_substrings: List<MatchedSubstring>,
    val place_id: String,
    val reference: String,
    val structured_formatting: StructuredFormatting,
    val terms: List<Term>,
    val types: List<String>
)

data class MatchedSubstring(
    val length: Int,
    val offset: Int
)

data class StructuredFormatting(
    val main_text: String,
    val main_text_matched_substrings: List<MatchedSubstring>,
)

data class Term(
    val offset: Int,
    val value: String
)