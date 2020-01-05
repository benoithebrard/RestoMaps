package com.bempaaa.restomaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bempaaa.restomaps.BuildConfig.APPLICATION_ID
import com.bempaaa.restomaps.data.RestoLocation
import com.bempaaa.restomaps.viewmodels.RestoMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE

private const val ORIGINAL_ZOOM_LEVEL = 13f

class RestoMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: RestoMapViewModel

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
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel = ViewModelProviders.of(this).get(RestoMapViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun GoogleMap.startAtLocation(location: LatLng) {
        with(this) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(location, ORIGINAL_ZOOM_LEVEL))
            addMarker(MarkerOptions().position(location).title("initial location"))

            setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                true
            }

            setOnInfoWindowClickListener { marker ->
                Toast.makeText(this@RestoMapsActivity, marker.title, Toast.LENGTH_LONG).show()
            }

            viewModel.restoMarkers.observe(this@RestoMapsActivity, Observer<List<MarkerOptions>> { restoMarkers ->
                restoMarkers.addToMap(this)
            })

            setOnCameraIdleListener {
                val cameraBounds = projection.visibleRegion.latLngBounds
                viewModel.fetchRestoMarkers(cameraBounds.northeast, cameraBounds.southwest)
            }
        }
    }

    private fun List<MarkerOptions>.addToMap(map: GoogleMap) =
        with(map) {
            forEach { addMarker(it) }
        }

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener { taskLocation ->
            if (taskLocation.isSuccessful) {
                currentLocation = taskLocation.result?.let { location ->
                    LatLng(location.latitude, location.longitude)
                }
            } else {
                showSnackbar(R.string.no_location_detected)
            }
        }
    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content), getString(snackStrId),
            LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }

    private fun checkPermissions() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() = ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
        REQUEST_PERMISSIONS_REQUEST_CODE
    )

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            showSnackbar(R.string.permission_rationale, android.R.string.ok, View.OnClickListener {
                // Request permission
                startLocationPermissionRequest()
            })

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation()
                else -> {
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = createSettingsIntent()
                            startActivity(intent)
                        })
                }
            }
        }
    }

    private fun createSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", APPLICATION_ID, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}
