package com.bempaaa.restomaps.viewmodels

import androidx.lifecycle.*
import com.bempaaa.restomaps.data.*
import com.bempaaa.restomaps.data.Configuration.restoService
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class RestoMapViewModel : ViewModel() {
    private val repository = RestoMapsRepository(restoService)

    private val restoVenues: MutableLiveData<List<RestoVenue>> by lazy {
        MutableLiveData<List<RestoVenue>>()
    }

    val restoMarkers: LiveData<List<MarkerOptions>> =
        Transformations.map(restoVenues) { restoVenues ->
            restoVenues.toMarkers()
        }

    fun fetchRestoMarkers(cameraBounds: LatLngBounds) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val venues = repository.searchForVenues(
                    sw = cameraBounds.southwest,
                    ne = cameraBounds.northeast
                ).response.venues
                restoVenues.postValue(venues)
            } catch (e: IOException) {
                val tot = 0
            }
        }
    }
}