package com.bempaaa.restomaps.data

import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

private const val INTENT_BROWSE = "browse"
private const val FOOD_CATEGORY_ID: String = "4d4b7105d754a06374d81259"
private const val CLIENT_ID = "CTWSBDMSCYHJBI5NKVZ1KGFONX3A5CRSSWPRNYN0Z1EQNQMK"
private const val CLIENT_SECRET = "UXPQDL1RZ1MF0A0DUIUSSE35Q5KG04JWBOCF5URGO2IPJAI1"

interface FourSquareSearchService {

    @GET("venues/search")
    suspend fun searchForVenues(
        @Query("sw") swLatLng: String,
        @Query("ne") neLatLng: String,
        @Query("intent") intent: String = INTENT_BROWSE,
        @Query("categoryId") categoryId: String = FOOD_CATEGORY_ID,
        @Query("client_id") clientId: String = CLIENT_ID,
        @Query("client_secret") clientSecret: String = CLIENT_SECRET,
        @Query("v") version: String = getCurrentDate()
    ): FourSquareSearchResponse
}

private fun getCurrentDate(): String =
    Calendar.getInstance().time.toString("yyyyMMdd")

private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}