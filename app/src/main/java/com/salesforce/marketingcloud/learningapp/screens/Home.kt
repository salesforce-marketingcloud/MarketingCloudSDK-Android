/**
 * Copyright 2019 Salesforce, Inc.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
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
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.google.android.material.materialswitch.MaterialSwitch
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.NotificationManager
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.learningapp.hasRequiredPermissions
import com.salesforce.marketingcloud.learningapp.showPermissionRationale
import com.salesforce.marketingcloud.mobileappmessaging.MobileAppMessaging
import com.salesforce.marketingcloud.pushfeature.PushFeature
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk

class Home : SdkFragment() {
    override val layoutId: Int get() = R.layout.fragment_home
    private lateinit var mceSdk: MarketingCloudSdk

    override fun ready(mceSdk: MarketingCloudSdk) {
        this.mceSdk = mceSdk

        if (requireContext().hasRequiredPermissions(NOTIFICATION_REQUIRED_PERMISSIONS)) {
            togglePushPermission(true)
        } else {
            checkAndRequestNotificationPermission()
        }

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        togglePushPermission(
            requireContext().hasRequiredPermissions(
                NOTIFICATION_REQUIRED_PERMISSIONS
            )
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                togglePushPermission(allPermissionsGranted)
            }
        }
    }

    private fun setupUI() {
        val navController = findNavController()
        with(requireView()) {
            findViewById<Button>(R.id.button_set_registration).setOnClickListener {
                navController.navigate(
                    HomeDirections.actionHomeToRegistration()
                )
            }
            findViewById<Button>(R.id.button_open_documentation).setOnClickListener {
                CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor)).build()
                    .launchUrl(context, DOCUMENTATION_URL.toUri())
            }
            findViewById<Button>(R.id.button_location).setOnClickListener {
                navController.navigate(
                    HomeDirections.actionHomeToLocation()
                )
            }
            findViewById<Button>(R.id.button_inbox).setOnClickListener {
                navController.navigate(
                    HomeDirections.actionHomeToInbox()
                )
            }


            findViewById<Button>(R.id.button_show_registration).setOnClickListener {
                navController.navigate(
                    HomeDirections.actionHomeToViewRegistration()
                )
            }
            findViewById<Button>(R.id.button_debug_sdk_state).setOnClickListener {
                showSdkStateDebug()
            }
            findViewById<Button>(R.id.button_tracking).setOnClickListener {
                navController.navigate(
                    HomeDirections.actionHomeToCustomEventTracking()
                )
            }

            // Device Data buttons
            findViewById<Button>(R.id.button_copy_mam_device_id).setOnClickListener {
                copyMamDeviceId()
            }
            findViewById<Button>(R.id.button_copy_push_token).setOnClickListener {
                copyPushToken()
            }
            findViewById<Button>(R.id.button_copy_mce_device_id).setOnClickListener {
                copyMceDeviceId()
            }

            setupAnalyticsToggle(this)
            setupInboxToggle(this)
            setupPiAnalyticsToggle(this)

        }
    }

    private fun setupAnalyticsToggle(view: View) {
        view.findViewById<MaterialSwitch>(R.id.switch_et_analytics).apply {
            isChecked = mceSdk.analyticsManager.areAnalyticsEnabled()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mceSdk.analyticsManager.enableAnalytics()
                } else {
                    mceSdk.analyticsManager.disableAnalytics()
                }
            }
        }
    }

    private fun setupInboxToggle(view: View) {
        view.findViewById<MaterialSwitch>(R.id.switch_inbox).apply {
            isChecked = mceSdk.inboxMessageManager.isInboxEnabled
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mceSdk.inboxMessageManager.enableInbox()
                } else {
                    mceSdk.inboxMessageManager.disableInbox()
                }
            }
        }
    }

    private fun setupPiAnalyticsToggle(view: View) {
        view.findViewById<MaterialSwitch>(R.id.switch_pi_analytics).apply {
            isChecked = mceSdk.analyticsManager.arePiAnalyticsEnabled()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mceSdk.analyticsManager.enablePiAnalytics()
                } else {
                    mceSdk.analyticsManager.disablePiAnalytics()
                }
            }
        }
    }


    private fun checkAndRequestNotificationPermission() {
        NOTIFICATION_REQUIRED_PERMISSIONS.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    permission.showPermissionRationale(requireContext()) {
                        requestNotificationPermission()
                    }
                } else {
                    requestNotificationPermission()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        @Suppress("DEPRECATION")
        requestPermissions(
            NOTIFICATION_REQUIRED_PERMISSIONS,
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun togglePushPermission(granted: Boolean) = PushFeature.requestSdk { sdk ->
        sdk.getPushMessageManager().apply {
            if (granted && !isPushEnabled()) {
                enablePush()
            } else if (!granted && isPushEnabled()) {
                disablePush()
            }
        }
    }

    private fun showSdkStateDebug() {
        SFMCSdk.requestSdk {
            showSdkStateDialog(it.getSdkState().toString(2).also {
                Log.i(TAG, "SDK State: $it")
            })
        }
    }

    private fun showSdkStateDialog(stateText: String) {
        // Create a scrollable text view for the dialog
        val scrollView = ScrollView(requireContext())
        val textView = TextView(requireContext()).apply {
            text = stateText
            textSize = 12f
            typeface = android.graphics.Typeface.MONOSPACE
            setPadding(32, 32, 32, 32)
            setTextIsSelectable(true)
        }
        scrollView.addView(textView)

        AlertDialog.Builder(requireContext())
            .setTitle("SDK State Debug")
            .setView(scrollView)
            .setPositiveButton("Copy to Clipboard") { _, _ ->
                val clipboard =
                    requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("SDK State", stateText)
                clipboard.setPrimaryClip(clip)
                NotificationManager.showSuccess("SDK state copied to clipboard")
            }
            .setNegativeButton("Close", null)
            .create()
            .show()
    }

    private fun copyMamDeviceId() {
        MobileAppMessaging.requestSdk { sdk ->
            val deviceId = sdk.getRegistrationManager().getDeviceId()
            copyToClipboard("MAM Device ID", deviceId)
            Log.i(TAG, "MAM Device ID: $deviceId")
            NotificationManager.showSuccess("MAM Device ID copied to clipboard")
        }
    }

    private fun copyPushToken() {
        PushFeature.requestSdk {
            val pushToken = it.getPushMessageManager().getPushToken()
            if (pushToken != null) {
                copyToClipboard("Push Token", pushToken)
                Log.i(TAG, "Push Token: $pushToken")
                NotificationManager.showSuccess("Push Token copied to clipboard")
            } else {
                Log.w(TAG, "Push Token is null")
                NotificationManager.showError("Push Token is not available")
            }
        }
    }

    private fun copyMceDeviceId() {
        MarketingCloudSdk.requestSdk { mceSdk ->
            val deviceId = mceSdk.registrationManager.deviceId
            copyToClipboard("MCE Device ID", deviceId)
            Log.i(TAG, "MCE Device ID: $deviceId")
            NotificationManager.showSuccess("MCE Device ID copied to clipboard")
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard =
            requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "~#Home"
        private const val DOCUMENTATION_URL =
            "https://developer.salesforce.com/docs/marketing/mobilepush/guide/overview.html"
        private val NOTIFICATION_REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                emptyArray()
            }
    }
}
