package com.bempaaa.restomaps

import com.bempaaa.restomaps.data.RestoMapsRepository
import com.bempaaa.restomaps.data.RestoVenue
import com.bempaaa.restomaps.data.SearchResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RestoMapUnitTest {
    private val repository = RestoMapsRepository(
        MockedSearchService(
            testLat = 10.00,
            testLng = 15.00,
            testName = "Ratatouille"
        )
    )

    @Test
    fun search_venue_in_bounds() = runBlockingTest {
        launch {
            repository.searchInBounds(
                bounds = LatLngBounds(
                    LatLng(09.00, 14.00),
                    LatLng(11.00, 16.00)
                )
            ) { result ->
                when (result) {
                    is SearchResult.Success -> {
                        assertEquals(1, result.restoVenues.size)
                        assertEquals("Ratatouille", result.restoVenues.getOrNull(0)?.name)
                    }
                    SearchResult.Loading -> {
                    }
                    SearchResult.Error -> {
                        fail("wrong search result")
                    }
                }
            }
        }
    }

    @Test
    fun get_cached_venue_in_bounds() = runBlockingTest {
        launch {
            repository.searchInBounds(
                bounds = LatLngBounds(
                    LatLng(09.00, 14.00),
                    LatLng(11.00, 16.00)
                )
            ) {}

            val cachedVenues: List<RestoVenue> = repository.getCachedVenuesInBounds(
                bounds = LatLngBounds(
                    LatLng(09.00, 14.00),
                    LatLng(11.00, 16.00)
                )
            )
            assertEquals(1, cachedVenues.size)
            assertEquals("Ratatouille", cachedVenues.getOrNull(0)?.name)
        }
    }

    @Test
    fun get_cached_venue_out_of_bounds() = runBlockingTest {
        launch {
            repository.searchInBounds(
                bounds = LatLngBounds(
                    LatLng(09.00, 14.00),
                    LatLng(11.00, 16.00)
                )
            ) {}

            val cachedVenues: List<RestoVenue> = repository.getCachedVenuesInBounds(
                bounds = LatLngBounds(
                    LatLng(01.00, 01.00),
                    LatLng(02.00, 02.00)
                )
            )
            assertEquals(0, cachedVenues.size)
        }
    }
}
