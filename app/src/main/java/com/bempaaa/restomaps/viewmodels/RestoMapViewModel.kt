package com.bempaaa.restomaps.viewmodels

import androidx.lifecycle.*
import com.bempaaa.restomaps.data.*
import com.bempaaa.restomaps.data.Configuration.restoService
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestoMapViewModel : ViewModel() {
    private val repository = RestoMapsRepository(restoService)

    private val searchResult: MutableLiveData<SearchResult> by lazy {
        MutableLiveData<SearchResult>(SearchResult.Loading)
    }

    val infoText: LiveData<String?> =
        Transformations.map(searchResult) { result ->
            when (result) {
                is SearchResult.Loading -> "loading.."
                is SearchResult.Error -> "error fetching data"
                else -> null
            }
        }

    val restoMarkers: LiveData<List<MarkerOptions>> =
        Transformations.map(searchResult) { result ->
            when (result) {
                is SearchResult.Success -> result.restoVenues.toMarkers()
                else -> emptyList()
            }
        }

    fun fetchRestoMarkers(bounds: LatLngBounds) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchInBounds(bounds) { result ->
                searchResult.postValue(result)
            }
        }
    }

    fun getCachedVenueByName(name: String) = repository.getCachedVenueByName(name)
}