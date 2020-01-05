package com.bempaaa.restomaps.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bempaaa.restomaps.data.RestoLocation
import com.bempaaa.restomaps.data.toMarkers
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RestoMapViewModel : ViewModel() {
    private val restoLocations: MutableLiveData<List<RestoLocation>> by lazy {
        MutableLiveData<List<RestoLocation>>()
    }

    val restoMarkers: LiveData<List<MarkerOptions>> = Transformations.map(restoLocations) {
            restoLocations -> restoLocations.toMarkers()
    }

    fun fetchRestoMarkers(northeast: LatLng, southwest: LatLng) {
        restoLocations.value = listOf(
            RestoLocation(northeast, "northeast"),
            RestoLocation(southwest, "southwest")
        )
    }
}