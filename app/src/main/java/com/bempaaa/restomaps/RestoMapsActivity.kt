package com.bempaaa.restomaps

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class RestoMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val SYDNEY1 = LatLng(-33.862, 151.21)
    private val SYDNEY2 = LatLng(-33.9, 151.21)
    private val SYDNEY3 = LatLng(-33.862, 151.22)
    private val ZOOM_LEVEL = 13f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY1, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(SYDNEY1).title("marker 1"))
            addMarker(MarkerOptions().position(SYDNEY2).title("marker 2"))
            addMarker(MarkerOptions().position(SYDNEY3).title("marker 3"))

            setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                true
            }

            setOnInfoWindowClickListener { marker ->
                Toast.makeText(this@RestoMapsActivity, marker.title, Toast.LENGTH_LONG).show()
            }

            setOnCameraIdleListener {
                val bounds = googleMap.projection.visibleRegion.latLngBounds
                addMarker(MarkerOptions().position(bounds.northeast).title("marker 4"))
                addMarker(MarkerOptions().position(bounds.southwest).title("marker 5"))
                addMarker(MarkerOptions().position(LatLng(bounds.northeast.latitude, bounds.southwest.longitude)).title("marker 6"))
                addMarker(MarkerOptions().position(LatLng(bounds.southwest.latitude, bounds.northeast.longitude)).title("marker 7"))
            }
        }
    }
}
