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

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.NotificationManager
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.sfmcsdk.components.events.EventManager

class CustomEventTracking : SdkFragment() {

    override val layoutId: Int get() = R.layout.fragment_custom_event_tracking

    private lateinit var mceSdk: MarketingCloudSdk
    private lateinit var eventsContainer: LinearLayout
    private lateinit var addEventButton: MaterialButton

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    data class StoredCustomEvent(
        val id: String,
        val name: String,
        val attributes: Map<String, String>
    )

    companion object {
        private const val TAG = "~#CustomEventTracking"
        private const val PREFS_NAME = "custom_events"
        private const val EVENTS_KEY = "stored_events"
        
        // TypeToken instances as companion object properties - more reliable with R8/ProGuard
        private val eventListTypeToken = object : TypeToken<List<StoredCustomEvent>>() {}
        private val mutableEventListTypeToken = object : TypeToken<MutableList<StoredCustomEvent>>() {}
    }

    override fun ready(mceSdk: MarketingCloudSdk) {
        this.mceSdk = mceSdk
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setupUI()
        loadStoredEvents()
    }

    override fun onResume() {
        super.onResume()
        // Only refresh events if UI is already initialized
        if (::eventsContainer.isInitialized) {
            loadStoredEvents()
        }
    }

    private fun setupUI() {
        with(requireView()) {
            eventsContainer = findViewById(R.id.events_container)
            addEventButton = findViewById(R.id.button_add_event)

            addEventButton.setOnClickListener {
                showCreateEventDialog()
            }
        }
    }

    private fun loadStoredEvents() {
        // Clear existing views
        eventsContainer.removeAllViews()

        val eventsJson = sharedPreferences.getString(EVENTS_KEY, "[]")
        val events: List<StoredCustomEvent> = gson.fromJson(eventsJson, eventListTypeToken.type)

        Log.v(TAG, "Loading ${events.size} stored events")

        if (events.isEmpty()) {
            // Show empty state
            val emptyView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_no_events, eventsContainer, false)
            eventsContainer.addView(emptyView)
        } else {
            // Add event cards
            events.forEachIndexed { index, event ->
                addEventCard(event, isLastCard = index == events.size - 1)
            }
        }
    }

    private fun addEventCard(event: StoredCustomEvent, isLastCard: Boolean = false) {
        val cardView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_custom_event_card, eventsContainer, false) as MaterialCardView

        // Set event name
        cardView.findViewById<android.widget.TextView>(R.id.event_name).text = event.name

        // Populate attributes display
        val attributesContainer =
            cardView.findViewById<LinearLayout>(R.id.attributes_display_container)
        attributesContainer.removeAllViews()

        if (event.attributes.isEmpty()) {
            val noAttributesText = android.widget.TextView(requireContext()).apply {
                text = "No attributes"
                textSize = 12f
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        context,
                        android.R.color.darker_gray
                    )
                )
            }
            attributesContainer.addView(noAttributesText)
        } else {
            event.attributes.forEach { (key, value) ->
                val attributeText = android.widget.TextView(requireContext()).apply {
                    text = "$key = $value"
                    textSize = 12f
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall)
                    setPadding(0, 2, 0, 2)
                }
                attributesContainer.addView(attributeText)
            }
        }

        // Set up buttons
        cardView.findViewById<MaterialButton>(R.id.button_edit_event).setOnClickListener {
            showEditEventDialog(event)
        }

        cardView.findViewById<MaterialButton>(R.id.button_track_event).setOnClickListener {
            trackCustomEvent(event)
        }

        cardView.findViewById<MaterialButton>(R.id.button_delete_event).setOnClickListener {
            showDeleteEventDialog(event)
        }

        // Apply proper bottom margin for the last card
        if (isLastCard) {
            val layoutParams = cardView.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.bottomMargin =
                resources.getDimensionPixelSize(R.dimen.last_card_bottom_margin)
            cardView.layoutParams = layoutParams
        }

        eventsContainer.addView(cardView)
    }

    private fun showCreateEventDialog() {
        showEventDialog(null)
    }

    private fun showEditEventDialog(event: StoredCustomEvent) {
        showEventDialog(event)
    }

    private fun showEventDialog(existingEvent: StoredCustomEvent?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_event, null)

        val eventNameInput = dialogView.findViewById<TextInputEditText>(R.id.input_event_name)
        val attributesContainer = dialogView.findViewById<LinearLayout>(R.id.attributes_container)
        val addAttributeButton = dialogView.findViewById<MaterialButton>(R.id.button_add_attribute)

        // Pre-populate if editing
        existingEvent?.let { event ->
            eventNameInput.setText(event.name)
            event.attributes.forEach { (key, value) ->
                addAttributeField(attributesContainer, key, value)
            }
        }

        // Add empty attribute field if creating new or no attributes exist
        if (existingEvent == null || existingEvent.attributes.isEmpty()) {
            addAttributeField(attributesContainer, "", "")
        }

        addAttributeButton.setOnClickListener {
            addAttributeField(attributesContainer, "", "")
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (existingEvent == null) "Create Custom Event" else "Edit Custom Event")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                saveEvent(existingEvent, eventNameInput, attributesContainer)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun addAttributeField(container: LinearLayout, key: String = "", value: String = "") {
        val fieldView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_attribute_field_with_delete, container, false)

        val keyInput = fieldView.findViewById<TextInputEditText>(R.id.input_attribute_key)
        val valueInput = fieldView.findViewById<TextInputEditText>(R.id.input_attribute_value)
        val deleteButton = fieldView.findViewById<MaterialButton>(R.id.button_delete_attribute)

        keyInput.setText(key)
        valueInput.setText(value)

        deleteButton.setOnClickListener {
            container.removeView(fieldView)
        }

        container.addView(fieldView)
    }

    private fun saveEvent(
        existingEvent: StoredCustomEvent?,
        eventNameInput: TextInputEditText,
        attributesContainer: LinearLayout
    ) {
        val eventName = eventNameInput.text.toString().trim()
        if (eventName.isEmpty()) {
            NotificationManager.showError("Event name is required")
            return
        }

        // Collect attributes
        val attributes = mutableMapOf<String, String>()
        for (i in 0 until attributesContainer.childCount) {
            val fieldView = attributesContainer.getChildAt(i)
            val keyInput = fieldView.findViewById<TextInputEditText>(R.id.input_attribute_key)
            val valueInput = fieldView.findViewById<TextInputEditText>(R.id.input_attribute_value)

            val key = keyInput.text.toString().trim()
            val value = valueInput.text.toString().trim()

            if (key.isNotEmpty() && value.isNotEmpty()) {
                attributes[key] = value
            }
        }

        // Create or update event
        val eventId = existingEvent?.id ?: java.util.UUID.randomUUID().toString()
        val newEvent = StoredCustomEvent(eventId, eventName, attributes)

        // Save to SharedPreferences
        val eventsJson = sharedPreferences.getString(EVENTS_KEY, "[]")
        val events: MutableList<StoredCustomEvent> = gson.fromJson(eventsJson, mutableEventListTypeToken.type)

        if (existingEvent != null) {
            // Update existing
            val index = events.indexOfFirst { it.id == existingEvent.id }
            if (index != -1) {
                events[index] = newEvent
            }
        } else {
            // Add new
            events.add(newEvent)
        }

        // Save back to preferences
        val updatedJson = gson.toJson(events)
        sharedPreferences.edit().putString(EVENTS_KEY, updatedJson).apply()

        Log.v(TAG, "Saved event: $newEvent")
        NotificationManager.showSuccess(if (existingEvent == null) "Event created" else "Event updated")

        // Refresh display
        loadStoredEvents()
    }

    private fun trackCustomEvent(event: StoredCustomEvent) {
        NotificationManager.showSuccess("Event '${event.name}' tracked successfully")
        EventManager.customEvent(event.name, event.attributes)?.track()
    }

    private fun showDeleteEventDialog(event: StoredCustomEvent) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete the event '${event.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteEvent(event)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun deleteEvent(event: StoredCustomEvent) {
        val eventsJson = sharedPreferences.getString(EVENTS_KEY, "[]")
        val events: MutableList<StoredCustomEvent> = gson.fromJson(eventsJson, mutableEventListTypeToken.type)

        events.removeAll { it.id == event.id }

        val updatedJson = gson.toJson(events)
        sharedPreferences.edit().putString(EVENTS_KEY, updatedJson).apply()

        Log.v(TAG, "Deleted event: ${event.name}")
        NotificationManager.showSuccess("Event '${event.name}' deleted")

        // Refresh display
        loadStoredEvents()
    }
}
