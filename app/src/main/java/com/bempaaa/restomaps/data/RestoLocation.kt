package com.bempaaa.restomaps.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

data class RestoLocation(
    val latLng: LatLng,
    val title: String
)

internal fun List<RestoLocation>.toMarkers(): List<MarkerOptions> = map { it.toMarker() }

private fun RestoLocation.toMarker(): MarkerOptions =
    MarkerOptions().position(latLng).title(title)