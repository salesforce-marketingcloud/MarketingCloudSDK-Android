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
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.learningapp.hasRequiredPermissions
import com.salesforce.marketingcloud.learningapp.showPermissionRationale
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk.Companion.requestSdk
import com.salesforce.marketingcloud.sfmcsdk.modules.ModuleIdentifier
import com.salesforce.marketingcloud.sfmcsdk.modules.push.PushModuleInterface

class Home : SdkFragment() {
    override val layoutId: Int get() = R.layout.fragment_home
    private lateinit var sfmcSdk: SFMCSdk

    override fun ready(sfmcSdk: SFMCSdk) {
        this.sfmcSdk = sfmcSdk

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
                notifyUserOfResults(permissions, grantResults)
            }
        }
    }

    private fun setupUI() {
        val navController = findNavController()
        with(requireView()) {
            findViewById<Button>(R.id.button_set_registration).setOnClickListener {
                setRegistrationValues(
                    sfmcSdk
                )
            }
            findViewById<Button>(R.id.button_open_documentation).setOnClickListener {
                CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor)).build()
                    .launchUrl(context, Uri.parse(DOCUMENTATION_URL))
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

            sfmcSdk.mp { push ->
                findViewById<Button>(R.id.button_show_registration).setOnClickListener {
                    showRegistration(
                        push
                    )
                }
                setupAnalyticsToggle(this, push)
                setupInboxToggle(this, push)
                setupPiAnalyticsToggle(this, push)
            }
        }
    }

    private fun setupAnalyticsToggle(view: View, push: PushModuleInterface) {
        view.findViewById<SwitchCompat>(R.id.switch_et_analytics).apply {
            isChecked = push.analyticsManager.areAnalyticsEnabled()
            setOnCheckedChangeListener { _, isChecked -> if (isChecked) push.analyticsManager.enableAnalytics() else push.analyticsManager.disableAnalytics() }
        }
    }

    private fun setupInboxToggle(view: View, push: PushModuleInterface) {
        view.findViewById<SwitchCompat>(R.id.switch_inbox).apply {
            isChecked = push.inboxMessageManager.isInboxEnabled
            setOnCheckedChangeListener { _, isChecked -> if (isChecked) push.inboxMessageManager.enableInbox() else push.inboxMessageManager.disableInbox() }
        }
    }

    private fun setupPiAnalyticsToggle(view: View, push: PushModuleInterface) {
        view.findViewById<SwitchCompat>(R.id.switch_pi_analytics).apply {
            isChecked = push.analyticsManager.arePiAnalyticsEnabled()
            setOnCheckedChangeListener { _, isChecked -> if (isChecked) push.analyticsManager.enablePiAnalytics() else push.analyticsManager.disablePiAnalytics() }
        }
    }

    private fun notifyUserOfResults(permissions: Array<out String>, grantResults: IntArray) {
        val deniedPermissions = permissions
            .zip(grantResults.toList()) // Combine permissions with their grant results
            .filter { it.second != PackageManager.PERMISSION_GRANTED } // Filter out granted permissions
            .map { it.first }
        val message = if (deniedPermissions.isNotEmpty()) {
            "Denied permissions: ${deniedPermissions.joinToString(", ")}"
        } else {
            "All permissions granted."
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setRegistrationValues(sdk: SFMCSdk) {
        sdk.mp {
            it.registrationManager.registerForRegistrationEvents { registration ->
                Log.v(TAG, registration.toString())
                "Registration Updated".showSnackbar()
            }
        }

        sdk.identity.setProfile(
            profileId = "salesforce.developer@example.com",
            attributes = mapOf("FirstName" to "Salesforce", "LastName" to "Developer"),
            module = ModuleIdentifier.PUSH
        )

        sdk.mp {
            it.registrationManager.edit().addTag("Android Learning App").commit()
        }
    }

    @SuppressLint("InflateParams")
    private fun showRegistration(push: PushModuleInterface) {
        val currentRegistration =
            push.state.optJSONObject("RegistrationManager")?.optJSONObject("current_registration")
        val message = currentRegistration?.toString(2) ?: "Unable to get current registration"

        MaterialAlertDialogBuilder(requireContext()).apply {
            val view =
                LayoutInflater.from(context)
                    .inflate(R.layout.selectable_textview, null, false) as TextView
            view.text = message
            setView(view)
            setPositiveButton("Close", null)
        }.show()
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

    private fun togglePushPermission(granted: Boolean) = requestSdk { sdk ->
        sdk.mp { push ->
            if (granted) push.pushMessageManager.enablePush() else push.pushMessageManager.disablePush()
        }
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
