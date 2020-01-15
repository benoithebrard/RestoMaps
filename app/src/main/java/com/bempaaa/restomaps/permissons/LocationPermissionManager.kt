package com.bempaaa.restomaps.permissons

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import com.bempaaa.restomaps.BuildConfig
import com.bempaaa.restomaps.R
import com.google.android.material.snackbar.Snackbar

private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

class LocationPermissionManager(private val activity: Activity) {

    fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            showSnackbar(R.string.permission_rationale, android.R.string.ok, View.OnClickListener {
                startLocationPermissionRequest()
            })

        } else startLocationPermissionRequest()
    }

    fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content), activity.getString(snackStrId),
            Snackbar.LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(activity.getString(actionStrId), listener)
        }
        snackbar.show()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        getLastLocation: () -> Unit
    ) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation()
                else -> {
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        View.OnClickListener {
                            val intent = createSettingsIntent()
                            activity.startActivity(intent)
                        })
                }
            }
        }
    }

    private fun startLocationPermissionRequest() = ActivityCompat.requestPermissions(
        activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
        REQUEST_PERMISSIONS_REQUEST_CODE
    )

    private fun createSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}