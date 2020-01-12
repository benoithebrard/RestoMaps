package com.bempaaa.restomaps.data

import com.google.android.gms.maps.model.LatLng

class RestoMapsRepository(private val service: FourSquareSearchService) {

    suspend fun searchForVenues(sw: LatLng, ne: LatLng) = service.searchForVenues(
        swLatLng = sw.preFormat(),
        neLatLng = ne.preFormat()
    )
}

private fun LatLng.preFormat(): String =
    "$latitude,$longitude"