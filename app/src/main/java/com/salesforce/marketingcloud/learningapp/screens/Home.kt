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
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk.Companion.requestSdk
import com.salesforce.marketingcloud.sfmcsdk.modules.ModuleIdentifier

private const val DOCUMENTATION_URL =
    "https://salesforce-marketingcloud.github.io/MarketingCloudSDK-Android/"
private val NOTIFICATION_REQUIRED_PERMISSIONS =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf()
    }

class Home : SdkFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_home

    private lateinit var sfmcSdk: SFMCSdk

    override fun ready(sfmcSdk: SFMCSdk) {
        this.sfmcSdk = sfmcSdk

        if (context != null && (context as Context).hasRequiredPermissions()) {
            togglePushPermission(true)
        } else {
            checkAndRequestNotificationPermission()
        }

        requireView().apply {
            findViewById<Button>(R.id.button_set_registration).setOnClickListener {
                setRegistrationValues(sfmcSdk)
            }
            findViewById<Button>(R.id.button_open_documentation).setOnClickListener {
                CustomTabsIntent.Builder().apply {
                    setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
                }.build().launchUrl(context, Uri.parse(DOCUMENTATION_URL))
            }

            val navController = findNavController()
            findViewById<Button>(R.id.button_location).setOnClickListener {
                navController.navigate(HomeDirections.actionHomeToLocation())
            }
            findViewById<Button>(R.id.button_inbox).setOnClickListener {
                navController.navigate(HomeDirections.actionHomeToInbox())
            }

            sfmcSdk.mp { mp ->
                findViewById<Button>(R.id.button_show_registration).setOnClickListener {
                    showRegistration(mp as MarketingCloudSdk)
                }

                val etAnalyticsToggle = findViewById<SwitchCompat>(R.id.switch_et_analytics)
                etAnalyticsToggle.isChecked = mp.analyticsManager.areAnalyticsEnabled()
                etAnalyticsToggle.setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        mp.analyticsManager.enableAnalytics()
                    } else {
                        mp.analyticsManager.disableAnalytics()
                    }
                }

                val inboxToggle = findViewById<SwitchCompat>(R.id.switch_inbox)
                inboxToggle.isChecked = mp.inboxMessageManager.isInboxEnabled
                inboxToggle.setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        mp.inboxMessageManager.enableInbox()
                    } else {
                        mp.inboxMessageManager.disableInbox()
                    }
                }

                val piAnalyticsToggle = findViewById<SwitchCompat>(R.id.switch_pi_analytics)
                piAnalyticsToggle.isChecked = mp.analyticsManager.arePiAnalyticsEnabled()
                piAnalyticsToggle.setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        mp.analyticsManager.enablePiAnalytics()
                    } else {
                        mp.analyticsManager.disablePiAnalytics()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with notifications
            } else {
                // Permission denied
                Toast.makeText(
                    requireContext(),
                    "Permission denied. You won't receive notifications.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermissionStatus()
    }

    private fun setRegistrationValues(sdk: SFMCSdk) {
        sdk.mp {
            it.registrationManager.registerForRegistrationEvents { registration ->
                "Registration Updated".showSnackbar()
                Log.v(TAG, registration.toString())
            }
        }

        sdk.identity.setProfile(
            profileId = "salesforce.developer@example.com",
            attributes = mapOf("FirstName" to "Salesforce", "LastName" to "Developer"),
            module = ModuleIdentifier.PUSH /* , modules = ModuleIdentifier.CDP */
        )

        sdk.mp {
            it.registrationManager.edit().addTag("Android Learning App").commit()
        }
    }

    private fun showRegistration(sdk: MarketingCloudSdk) {
        val currentRegistration = sdk.sdkState
            .optJSONObject("RegistrationManager")
            ?.optJSONObject("current_registration")

        MaterialAlertDialogBuilder(requireContext()).apply {
            val view =
                LayoutInflater.from(context).inflate(R.layout.selectable_textview, null) as TextView
            view.text =
                if (currentRegistration != null) currentRegistration.toString(2) else "Unable to get current registration"
            setView(view)
            setPositiveButton("Close", null)
        }.show()
    }

    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Show rationale
                AlertDialog.Builder(requireContext())
                    .setTitle("Notification Permission Needed")
                    .setMessage("This app needs notification permission to send alerts.")
                    .setPositiveButton("OK") { _, _ ->
                        requestNotificationPermission()
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()
            } else {
                // Directly request the permission
                requestNotificationPermission()
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun requestNotificationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkNotificationPermissionStatus() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            togglePushPermission(true)
        } else {
            togglePushPermission(false)
        }
    }

    private fun togglePushPermission(granted: Boolean) = requestSdk { sdk ->
        sdk.mp { push ->
            if (granted) push.pushMessageManager.enablePush() else push.pushMessageManager.disablePush()
        }
    }

    private fun Context.hasRequiredPermissions(): Boolean {
        return NOTIFICATION_REQUIRED_PERMISSIONS
            .map { ContextCompat.checkSelfPermission(this, it) }
            .all { it == PackageManager.PERMISSION_GRANTED }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "~#Home"
    }
}