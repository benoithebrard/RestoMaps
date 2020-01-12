package com.bempaaa.restomaps.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object Configuration {
    private const val FOUR_SQUARE_BASE_URL = "https://api.foursquare.com/v2/"

    val restoService: FourSquareSearchService by lazy {
        createRetrofit().create(FourSquareSearchService::class.java)
    }

    private fun createRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(
                Json(
                    JsonConfiguration(strictMode = false)
                ).asConverterFactory(contentType)
            )
            .baseUrl(FOUR_SQUARE_BASE_URL)
            .client(OkHttpClient())
            .build()
    }
}