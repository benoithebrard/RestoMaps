package com.bempaaa.restomaps

import com.bempaaa.restomaps.data.*

class MockedSearchService(
    private val testLat: Double,
    private val testLng: Double,
    private val testName: String
) : FourSquareSearchService {
    override suspend fun searchForVenues(
        swLatLng: String,
        neLatLng: String,
        intent: String,
        categoryId: String,
        clientId: String,
        clientSecret: String,
        version: String
    ) = FourSquareSearchResponse(
        response = SearchResponseItem(
            venues = listOf(
                RestoVenue(
                    name = testName,
                    location = RestoLocation(
                        lat = testLat,
                        lng = testLng
                    ),
                    categories = listOf(
                        RestoCategory(
                            name = "French restaurant"
                        )
                    )
                )
            )
        )
    )

}