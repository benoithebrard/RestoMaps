package com.bempaaa.restomaps.data

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.io.IOException

private const val MIN_CACHE_SIZE_CLEAN = 100
private const val EXTRA_CACHE_SIZE_CLEAN = MIN_CACHE_SIZE_CLEAN / 3

class RestoMapsRepository(private val service: FourSquareSearchService) {

    private val venueCache = HashMap<RestoLocation, RestoVenue>()

    suspend fun searchInBounds(
        bounds: LatLngBounds,
        onResult: (SearchResult) -> Unit
    ) {
        onResult(SearchResult.Loading)
        val cachedVenues = getCachedVenuesInBounds(bounds)

        if (cachedVenues.isNotEmpty()) {
            onResult(SearchResult.Success(cachedVenues))
        }

        try {
            val venues = searchVenuesInBounds(bounds)
            onResult(SearchResult.Success(venues))
        } catch (e: IOException) {
            onResult(SearchResult.Error)
        }
    }

    private suspend fun searchVenuesInBounds(bounds: LatLngBounds): List<RestoVenue> {
        if (venueCache.size > MIN_CACHE_SIZE_CLEAN) {
            val nbRemoved = venueCache.size - MIN_CACHE_SIZE_CLEAN + EXTRA_CACHE_SIZE_CLEAN
            Log.d("RestoMapsRepository", "== clear cache ($nbRemoved/${venueCache.size}) ==")
            cleanCache(bounds, nbRemoved)
        }

        val venues = service.searchForVenues(
            swLatLng = bounds.southwest.preFormat(),
            neLatLng = bounds.northeast.preFormat()
        ).response.venues

        storeInCache(venues)
        return venues
    }

    fun getCachedVenuesInBounds(bounds: LatLngBounds): List<RestoVenue> =
        venueCache.filterKeys { it.isInBounds(bounds) }.values.toList()

    private fun storeInCache(venues: List<RestoVenue>) {
        venueCache.putAll(venues.associateBy({ it.location }, { it }))
    }

    private fun cleanCache(
        bounds: LatLngBounds,
        nbRemoved: Int
    ) = venueCache.filterKeys { !it.isInBounds(bounds) }.keys
        .take(nbRemoved)
        .forEach { venueCache.remove(it) }
}

//sw=55.55,12.97&ne=55.61,13.04
private fun LatLng.preFormat(): String =
    "$latitude,$longitude"

private fun RestoLocation.isInBounds(
    bounds: LatLngBounds
) = isInBounds(bounds.southwest, bounds.northeast)

private fun RestoLocation.isInBounds(
    sw: LatLng,
    ne: LatLng
) = lat >= sw.latitude &&
        lat <= ne.latitude &&
        lng >= sw.longitude &&
        lng <= ne.longitude

sealed class SearchResult {
    object Loading : SearchResult()
    data class Success(val restoVenues: List<RestoVenue>) : SearchResult()
    object Error : SearchResult()
}
