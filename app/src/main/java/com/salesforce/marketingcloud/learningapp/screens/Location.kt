/**
 * Copyright 2019 Salesforce, Inc
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.marketingcloud.learningapp.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.LOG_TAG
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk


class Location : SdkFragment() {

    companion object {
        private const val REQUEST_GEOFENCE = 1
        private const val REQUEST_PROXIMITY = 2

        private val GEOFENCE_REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        private val PROXIMITY_REQUIRED_PERMISSIONS = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            }Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
            else -> {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private val geofenceCheckedListener = CompoundButton.OnCheckedChangeListener { _, checked ->
        toggleGeofenceMessaging(marketingCloudSdk, checked)
    }

    private val proximityCheckedListener = CompoundButton.OnCheckedChangeListener { _, checked ->
        toggleProximityMessaging(marketingCloudSdk, checked)
    }

    private lateinit var marketingCloudSdk: MarketingCloudSdk

    override val layoutId: Int
        get() = R.layout.fragment_location

    override fun ready(sfmcSdk: SFMCSdk) {
        sfmcSdk.mp {
            marketingCloudSdk = it as MarketingCloudSdk

            requireView().apply {
                setupGeofenceToggle(marketingCloudSdk)
                setupProximityToggle(marketingCloudSdk)
            }
        }
    }

    private fun View.setupGeofenceToggle(sdk: MarketingCloudSdk) {
        findViewById<SwitchMaterial>(R.id.toggle_geofence).apply {
            setOnCheckedChangeListener(null)
            isChecked = sdk.regionMessageManager.isGeofenceMessagingEnabled
            setOnCheckedChangeListener(geofenceCheckedListener)
        }
    }

    private fun View.setupProximityToggle(sdk: MarketingCloudSdk) {
        findViewById<SwitchMaterial>(R.id.toggle_proximity).apply {
            setOnCheckedChangeListener(null)
            isChecked = sdk.regionMessageManager.isProximityMessagingEnabled
            setOnCheckedChangeListener(proximityCheckedListener)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            when (requestCode) {
                REQUEST_GEOFENCE -> {
                    // Once the permissions have been granted we will enable Geofence messaging.
                    marketingCloudSdk.regionMessageManager.enableGeofenceMessaging()
                    view?.setupGeofenceToggle(marketingCloudSdk)
                }
                REQUEST_PROXIMITY -> {
                    // Once the permissions have been granted we will enable Proximity messaging.
                    marketingCloudSdk.regionMessageManager.enableProximityMessaging()
                    view?.setupProximityToggle(marketingCloudSdk)
                }
            }
        } else {
            when (requestCode) {
                REQUEST_GEOFENCE -> view?.setupGeofenceToggle(marketingCloudSdk)
                REQUEST_PROXIMITY -> view?.setupProximityToggle(marketingCloudSdk)
            }
            Log.d(
                LOG_TAG,
                "Not all required permissions for location messaging were granted. ${
                    permissions.zip(
                        grantResults.toTypedArray()
                    ).toMap()
                }"
            )
        }
    }

    private fun Context.hasRequiredPermissions(): Boolean {
        return GEOFENCE_REQUIRED_PERMISSIONS
            .map { ContextCompat.checkSelfPermission(this, it) }
            .all { it == PackageManager.PERMISSION_GRANTED }
    }

    @SuppressLint("MissingPermission")
    private fun toggleGeofenceMessaging(sdk: MarketingCloudSdk, enable: Boolean) {
        if (enable) {
            if (requireContext().hasRequiredPermissions()) {
                // If the required permissions are already granted then we will enable Geofence messaging in the SDK
                sdk.regionMessageManager.enableGeofenceMessaging()
            } else {
                requestPermissions(GEOFENCE_REQUIRED_PERMISSIONS, REQUEST_GEOFENCE)
            }
        } else {
            // Disable Geofence messaging in the SDK.
            sdk.regionMessageManager.disableGeofenceMessaging()
        }
    }

    @SuppressLint("MissingPermission")
    private fun toggleProximityMessaging(sdk: MarketingCloudSdk, enable: Boolean) {
        if (enable) {
            if (requireContext().hasRequiredPermissions()) {
                // If the required permissions are already granted then we will enable Proximity messaging in the SDK
                sdk.regionMessageManager.enableProximityMessaging()
            } else {
                requestPermissions(PROXIMITY_REQUIRED_PERMISSIONS, REQUEST_PROXIMITY)
            }
        } else {
            // Disable Proximity messaging in the SDK.
            sdk.regionMessageManager.disableProximityMessaging()
        }
    }
}