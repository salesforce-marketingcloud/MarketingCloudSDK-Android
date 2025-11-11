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

import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk

class ViewRegistration : SdkFragment() {
    override val layoutId: Int get() = R.layout.fragment_view_registration

    private lateinit var mceSdk: MarketingCloudSdk
    private lateinit var profileIdText: TextView
    private lateinit var partyIdNumText: TextView
    private lateinit var partyIdNameText: TextView
    private lateinit var partyIdTypeText: TextView
    private lateinit var attributesContainer: LinearLayout

    companion object {
        private const val TAG = "~#ViewRegistration"
    }

    override fun ready(mceSdk: MarketingCloudSdk) {
        this.mceSdk = mceSdk
        setupUI()
        loadRegistrationData()
    }

    private fun setupUI() {
        with(requireView()) {
            profileIdText = findViewById(R.id.text_profile_id)
            partyIdNumText = findViewById(R.id.text_party_id_num)
            partyIdNameText = findViewById(R.id.text_party_id_name)
            partyIdTypeText = findViewById(R.id.text_party_id_type)
            attributesContainer = findViewById(R.id.attributes_container)
        }
    }

    private fun loadRegistrationData() {
        // Load current identity from SFMC SDK
        SFMCSdk.requestSdk { sdk ->
            val identity = sdk.identity

            // Populate profile section
            profileIdText.text = identity.profileId?.takeIf { it.isNotEmpty() } ?: "Not set"

            // Populate party identification section
            partyIdNumText.text =
                identity.partyIdentificationNumber?.takeIf { it.isNotEmpty() } ?: "Not set"
            partyIdNameText.text =
                identity.partyIdentificationName?.takeIf { it.isNotEmpty() } ?: "Not set"
            partyIdTypeText.text =
                identity.partyIdentificationType?.takeIf { it.isNotEmpty() } ?: "Not set"

            // Populate attributes section
            populateAttributes(identity.attributes)

            Log.v(
                TAG,
                "Loaded registration data - Profile: ${identity.profileId}, Party ID: ${identity.partyIdentificationNumber}, Attributes: ${identity.attributes}"
            )
        }
    }

    private fun populateAttributes(attributes: Map<String, String?>) {
        // Clear existing attribute views
        attributesContainer.removeAllViews()

        if (attributes.isEmpty()) {
            // Show "No attributes" message
            val noAttributesView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_no_attributes, attributesContainer, false)
            attributesContainer.addView(noAttributesView)
        } else {
            // Add each attribute as a key-value display
            attributes.forEach { (key, value) ->
                addAttributeDisplay(key, value ?: "null")
            }
        }
    }

    private fun addAttributeDisplay(key: String, value: String) {
        val attributeView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_attribute_display, attributesContainer, false)

        val keyText = attributeView.findViewById<TextView>(R.id.text_attribute_key)
        val valueText = attributeView.findViewById<TextView>(R.id.text_attribute_value)

        keyText.text = key
        valueText.text = value

        attributesContainer.addView(attributeView)
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to screen
        loadRegistrationData()
    }
}
