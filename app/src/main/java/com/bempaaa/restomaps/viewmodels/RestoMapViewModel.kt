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

    private val fetchResult: MutableLiveData<FetchResult> by lazy {
        MutableLiveData<FetchResult>(FetchResult.Loading)
    }

    val infoText: LiveData<String?> =
        Transformations.map(fetchResult) { result ->
            when (result) {
                is FetchResult.Loading -> "loading.."
                is FetchResult.Error -> "error fetching data"
                else -> null
            }
        }

    val restoMarkers: LiveData<List<MarkerOptions>> =
        Transformations.map(fetchResult) { result ->
            when (result) {
                is FetchResult.Success -> result.restoVenues.toMarkers()
                else -> emptyList()
            }
        }

    fun fetchRestoMarkers(bounds: LatLngBounds) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: move this logic to the repository instead
            fetchResult.postValue(FetchResult.Loading)

            val cachedVenues = repository.getCachedVenues(bounds)
            fetchResult.postValue(FetchResult.Success(cachedVenues))

            try {
                val venues = repository.searchForVenues(bounds)
                fetchResult.postValue(FetchResult.Success(venues))
            } catch (e: IOException) {
                fetchResult.postValue(FetchResult.Error)
            }
        }
    }

    fun getCachedVenueByName(name: String) = repository.getCachedVenueByName(name)

    sealed class FetchResult {
        object Loading : FetchResult()
        data class Success(val restoVenues: List<RestoVenue>) : FetchResult()
        object Error : FetchResult()
    }
}