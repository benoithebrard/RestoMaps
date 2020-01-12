package com.bempaaa.restomaps.data

import com.google.android.gms.maps.model.LatLng

//sw=55.55,12.97&ne=55.61,13.04

class RestoMapsRepository(private val service: FourSquareSearchService) {

    private val venueCache = HashMap<RestoLocation, RestoVenue>()

    suspend fun searchForVenues(sw: LatLng, ne: LatLng): List<RestoVenue> {
        val venues = service.searchForVenues(
            swLatLng = sw.preFormat(),
            neLatLng = ne.preFormat()
        ).response.venues

        venueCache.putAll(venues.associateBy({ it.location }, { it }))
        return venues
    }

    fun getCachedVenues(sw: LatLng, ne: LatLng): List<RestoVenue> = venueCache.mapNotNull {
        val location = it.key
        if (location.lat >= sw.latitude &&
            location.lat <= ne.latitude &&
            location.lng >= sw.longitude &&
            location.lng <= ne.longitude
        ) it.value else null
    }
}

private fun LatLng.preFormat(): String =
    "$latitude,$longitude"