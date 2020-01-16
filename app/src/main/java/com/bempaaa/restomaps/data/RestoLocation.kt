package com.bempaaa.restomaps.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FourSquareSearchResponse(
    @SerialName("response") val response: SearchResponseItem
)

@Serializable
data class SearchResponseItem(
    @SerialName("venues") val venues: List<RestoVenue>
)

@Serializable
data class RestoVenue(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("contact") val contact: RestoContact? = null,
    @SerialName("location") val location: RestoLocation,
    @SerialName("categories") val categories: List<RestoCategory>,
    @SerialName("url") val url: String? = null
)

@Serializable
data class RestoCategory(
    @SerialName("name") val name: String
)

@Serializable
data class RestoContact(
    @SerialName("phone") val phone: String
)

@Serializable
data class RestoLocation(
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("address") val address: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    @SerialName("city") val city: String? = null
)

internal fun List<RestoVenue>.toMarkers(): List<MarkerOptions> = map { it.toMarker() }

private fun RestoVenue.toMarker(): MarkerOptions =
    MarkerOptions().position(location.toLatLng()).title(name).snippet("tap to see more")

private fun RestoLocation.toLatLng(): LatLng =
    LatLng(lat, lng)