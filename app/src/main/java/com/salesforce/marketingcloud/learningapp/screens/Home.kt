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

import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment

private const val DOCUMENTATION_URL =
    "https://salesforce-marketingcloud.github.io/MarketingCloudSDK-Android/"

class Home : SdkFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_home

    private lateinit var marketingCloudSdk: MarketingCloudSdk

    override fun onSdkReady(sdk: MarketingCloudSdk) {
        marketingCloudSdk = sdk

        requireView().apply {
            findViewById<Button>(R.id.button_set_registration).setOnClickListener {
                setRegistrationValues(
                    sdk
                )
            }
            findViewById<Button>(R.id.button_show_registration).setOnClickListener {
                showRegistration(
                    sdk
                )
            }

            val navController = findNavController()

            findViewById<Button>(R.id.button_open_documentation).setOnClickListener {
                CustomTabsIntent.Builder().apply {
                    setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
                }.build().launchUrl(context, Uri.parse(DOCUMENTATION_URL))
            }

            findViewById<Button>(R.id.button_location).setOnClickListener {
                navController.navigate(HomeDirections.actionHomeToLocation())
            }

            findViewById<Button>(R.id.button_inbox).setOnClickListener {
                navController.navigate(HomeDirections.actionHomeToInbox())
            }


            val etAnalyticsToggle = findViewById<SwitchCompat>(R.id.switch_et_analytics)
            etAnalyticsToggle.isChecked = sdk.analyticsManager.areAnalyticsEnabled()
            etAnalyticsToggle.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    sdk.analyticsManager.enableAnalytics()
                } else {
                    sdk.analyticsManager.disableAnalytics()
                }
            }

            val inboxToggle = findViewById<SwitchCompat>(R.id.switch_inbox)
            inboxToggle.isChecked = sdk.inboxMessageManager.isInboxEnabled
            inboxToggle.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    sdk.inboxMessageManager.enableInbox()
                } else {
                    sdk.inboxMessageManager.disableInbox()
                }
            }

            val piAnalyticsToggle = findViewById<SwitchCompat>(R.id.switch_pi_analytics)
            piAnalyticsToggle.isChecked = sdk.analyticsManager.arePiAnalyticsEnabled()
            piAnalyticsToggle.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    sdk.analyticsManager.enablePiAnalytics()
                } else {
                    sdk.analyticsManager.disablePiAnalytics()
                }
            }
        }
    }

    private fun setRegistrationValues(sdk: MarketingCloudSdk) {

        // Update the registration with user data.  This information can then be used by a marketer
        // in the Marketing Cloud UI to target this device/user for messaging.
        val success = sdk.registrationManager.edit().apply {
            setContactKey("username@example.com")
            setAttribute("LastName", "Smith")
            addTag("Camping")
        }.commit()

        (if (success) "Registration updated" else "Registration unchanged").showSnackbar()
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
}