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
    @SerialName("name") val name: String,
    @SerialName("location") val location: RestoLocation
)

@Serializable
data class RestoLocation(
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double
)

internal fun List<RestoVenue>.toMarkers(): List<MarkerOptions> = map { it.toMarker() }

private fun RestoVenue.toMarker(): MarkerOptions =
    MarkerOptions().position(location.toLatLng()).title(name)

private fun RestoLocation.toLatLng(): LatLng =
    LatLng(lat, lng)