package com.bempaaa.restomaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bempaaa.restomaps.databinding.ActivityMapsBinding
import com.bempaaa.restomaps.permissons.LocationPermissionManager
import com.bempaaa.restomaps.viewmodels.RestoMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val ORIGINAL_ZOOM_LEVEL = 13f

class RestoMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val permissionManager = LocationPermissionManager(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var restoMapViewModel: RestoMapViewModel

    var map: GoogleMap? = null
        set(value) {
            if (field != value) {
                currentLocation?.let { location ->
                    value?.startAtLocation(location)
                }
                field = value
            }
        }

    var currentLocation: LatLng? = null
        set(value) {
            if (field != value) {
                value?.let { location ->
                    map?.startAtLocation(location)
                }
                field = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        restoMapViewModel = ViewModelProvider(this).get(RestoMapViewModel::class.java)

        DataBindingUtil.setContentView<ActivityMapsBinding>(this, R.layout.activity_maps).apply {
            viewModel = restoMapViewModel
            lifecycleOwner = this@RestoMapsActivity
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            permissionManager.requestPermissions()
        } else {
            getLastLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun GoogleMap.startAtLocation(location: LatLng) {
        with(this) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(location, ORIGINAL_ZOOM_LEVEL))

            setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                true
            }

            setOnInfoWindowClickListener { marker ->
                restoMapViewModel.getCachedVenue(marker.title)?.let { venue ->
                    val restoDetailsFragment = RestoDetailsFragment.newInstance(venue)
                    restoDetailsFragment.show(supportFragmentManager, restoDetailsFragment.tag)
                }
            }

            restoMapViewModel.restoMarkers.observe(
                this@RestoMapsActivity,
                Observer<List<MarkerOptions>> { restoMarkers ->
                    restoMarkers.addToMap(this)
                })

            setOnCameraIdleListener {
                val cameraBounds = projection.visibleRegion.latLngBounds
                restoMapViewModel.fetchRestoMarkers(cameraBounds)
            }
        }
    }

    private fun List<MarkerOptions>.addToMap(map: GoogleMap) =
        with(map) {
            forEach { addMarker(it) }
        }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener { taskLocation ->
            if (taskLocation.isSuccessful && taskLocation.result != null) {
                currentLocation = taskLocation.result?.let { location ->
                    LatLng(location.latitude, location.longitude)
                }
            } else {
                permissionManager.showSnackbar(R.string.no_location_detected)
            }
        }
    }

    private fun checkPermissions() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) = permissionManager.onRequestPermissionsResult(requestCode, grantResults, ::getLastLocation)
}
