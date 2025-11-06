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
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.NotificationManager
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.mobileappmessaging.MobileAppMessaging
import com.salesforce.marketingcloud.mobileappmessaging.registration.RegistrationManager
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import org.json.JSONObject

class Registration : SdkFragment() {
    override val layoutId: Int get() = R.layout.fragment_registration

    private lateinit var mceSdk: MarketingCloudSdk
    private lateinit var profileIdInput: TextInputEditText
    private lateinit var partyIdNumInput: TextInputEditText
    private lateinit var partyIdNameInput: TextInputEditText
    private lateinit var partyIdTypeInput: TextInputEditText
    private lateinit var attributesContainer: LinearLayout
    private lateinit var addAttributeButton: MaterialButton
    private lateinit var saveButton: MaterialButton

    private val attributeViews = mutableListOf<Pair<TextInputEditText, TextInputEditText>>()

    companion object {
        private const val TAG = "~#Registration"
    }

    override fun ready(mceSdk: MarketingCloudSdk) {
        this.mceSdk = mceSdk
        setupUI()
        loadCurrentValues()
    }

    private fun setupUI() {
        with(requireView()) {
            profileIdInput = findViewById(R.id.input_profile_id)
            partyIdNumInput = findViewById(R.id.input_party_id_num)
            partyIdNameInput = findViewById(R.id.input_party_id_name)
            partyIdTypeInput = findViewById(R.id.input_party_id_type)
            attributesContainer = findViewById(R.id.attributes_container)
            addAttributeButton = findViewById(R.id.button_add_attribute)
            saveButton = findViewById(R.id.button_save_registration)

            addAttributeButton.setOnClickListener {
                addAttributeField()
            }

            saveButton.setOnClickListener {
                saveRegistrationValues()
            }
        }
    }

    private fun loadCurrentValues() {
        // Clear existing attribute views
        attributesContainer.removeAllViews()
        attributeViews.clear()

        // Load current identity from SFMC SDK
        SFMCSdk.requestSdk { sdk ->
            val identity = sdk.identity

            // Populate identity fields
            profileIdInput.setText(identity.profileId ?: "")
            partyIdNumInput.setText(identity.partyIdentificationNumber ?: "")
            partyIdNameInput.setText(identity.partyIdentificationName ?: "")
            partyIdTypeInput.setText(identity.partyIdentificationType ?: "")

            // Load attributes
            val attributes = identity.attributes
            if (attributes.isNotEmpty()) {
                attributes.forEach { (key, value) ->
                    addAttributeField(key, value.toString())
                }
            } else {
                // Add default attributes if none exist
                addAttributeField("FirstName", "Salesforce")
                addAttributeField("LastName", "Developer")
            }

            Log.v(
                TAG,
                "Loaded identity - Profile: ${identity.profileId}, Party ID: ${identity.partyIdentificationNumber}, Attributes: $attributes"
            )
        }
    }

    private fun addAttributeField(key: String = "", value: String = "") {
        val attributeLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_attribute_field, attributesContainer, false) as LinearLayout

        val keyInput = attributeLayout.findViewById<TextInputEditText>(R.id.input_attribute_key)
        val valueInput = attributeLayout.findViewById<TextInputEditText>(R.id.input_attribute_value)
        val removeButton =
            attributeLayout.findViewById<MaterialButton>(R.id.button_remove_attribute)

        keyInput.setText(key)
        valueInput.setText(value)

        removeButton.setOnClickListener {
            attributesContainer.removeView(attributeLayout)
            attributeViews.removeAll { it.first == keyInput }
        }

        attributeViews.add(Pair(keyInput, valueInput))
        attributesContainer.addView(attributeLayout)
    }

    private fun saveRegistrationValues() {
        val profileId = profileIdInput.text.toString().trim()
        val partyIdNum = partyIdNumInput.text.toString().trim()
        val partyIdName = partyIdNameInput.text.toString().trim()
        val partyIdType = partyIdTypeInput.text.toString().trim()

        // Collect attributes
        val attributes = mutableMapOf<String, String>()
        attributeViews.forEach { (keyInput, valueInput) ->
            val key = keyInput.text.toString().trim()
            val value = valueInput.text.toString().trim()
            if (key.isNotEmpty() && value.isNotEmpty()) {
                attributes[key] = value
            }
        }

        // Register for registration events
        mceSdk.registrationManager.registerForRegistrationEvents { registration ->
            Log.v(TAG, "Marketing Cloud Registration Updated: $registration")
            NotificationManager.showSuccess("Marketing Cloud Registration Updated")
        }

        // Register for Mobile App Messaging events
        MobileAppMessaging.requestSdk { mamSdk ->
            mamSdk.getRegistrationManager().registerForRegistrationEvents(
                object : RegistrationManager.RegistrationEventListener {
                    override fun onRegistrationReceived(responseBody: JSONObject) {
                        Log.v(TAG, "Mobile App Messaging Registration Updated: $responseBody")
                        NotificationManager.showSuccess("Mobile App Messaging Registration Updated")
                    }
                }
            )
        }

        // Update SFMC SDK identity
        SFMCSdk.requestSdk { sdk ->
            sdk.identity.edit {
                if (profileId.isNotEmpty()) {
                    this.profileId = profileId
                }
                if (partyIdNum.isNotEmpty()) {
                    partyIdentificationNumber = partyIdNum
                }
                if (partyIdName.isNotEmpty()) {
                    partyIdentificationName = partyIdName
                }
                if (partyIdType.isNotEmpty()) {
                    partyIdentificationType = partyIdType
                }
                if (attributes.isNotEmpty()) {
                    this.attributes.set(attributes)
                }
            }
        }

        // Add tag to Marketing Cloud registration
        mceSdk.registrationManager.edit().addTag("Android Learning App").commit()

        Snackbar.make(
            requireView(),
            "Registration values saved successfully!",
            Snackbar.LENGTH_SHORT
        ).show()
        Log.v(
            TAG,
            "Registration saved - Profile: $profileId, Party ID: $partyIdNum, Attributes: $attributes"
        )

        // Navigate back to home screen
        findNavController().navigateUp()
    }
}
