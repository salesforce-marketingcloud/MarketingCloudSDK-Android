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

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.NotificationManager
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.messages.inbox.InboxMessage
import com.salesforce.marketingcloud.messages.inbox.InboxMessageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class Inbox : SdkFragment(), CoroutineScope, InboxMessageManager.InboxResponseListener,
    MenuProvider {

    private val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    private lateinit var mceSdk: MarketingCloudSdk
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: View

    private var job: Job? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val inboxAdapter = InboxAdapter(
        messageClickListener = { message ->
            // Mark the message as `read`.
            mceSdk.inboxMessageManager.setMessageRead(message)

            // Log an open analytics for the Inbox message.
            mceSdk.analyticsManager.trackInboxOpenEvent(message)

            // Open the message URL directly in Chrome Custom Tabs for better UX
            message.url?.let { url ->
                try {
                    CustomTabsIntent.Builder()
                        .setToolbarColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.primaryColor
                            )
                        )
                        .setShowTitle(true)
                        .build()
                        .launchUrl(requireContext(), url.toUri())
                } catch (e: Exception) {
                    NotificationManager.showError("Could not open message: ${e.message}")
                }
            } ?: run {
                NotificationManager.showInfo("This message has no content to display")
            }
        },
        messageLongClickListener = { message ->
            // Mark a message as deleted.  This will remove it from the messages returned from the SDK.
            mceSdk.inboxMessageManager.deleteMessage(message)
            fetchMessageFromSdk()
        },
        messageDeleteClickListener = { message ->
            // Show confirmation dialog before deleting
            showDeleteConfirmationDialog(message)
        })

    override val layoutId: Int
        get() = R.layout.fragment_inbox

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.inbox_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_refresh -> {
                refreshInbox()
                true
            }

            else -> false
        }
    }

    override fun onDestroyView() {
        if (::mceSdk.isInitialized) {
            mceSdk.inboxMessageManager.unregisterInboxResponseListener(this)
        }
        job?.cancel()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (::mceSdk.isInitialized) {
            fetchMessageFromSdk()
        }
    }

    override fun ready(mceSdk: MarketingCloudSdk) {
        this@Inbox.mceSdk = mceSdk

        // Here we are registering the Inbox fragment to receive callbacks when the SDK receives a
        // refreshed list of Inbox messages from the Marketing Cloud.
        mceSdk.inboxMessageManager.registerInboxResponseListener(this)

        val view = requireView()

        // Set up toolbar menu using the main activity's toolbar
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Set up RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_inbox).apply {
            adapter = inboxAdapter
            layoutManager = LinearLayoutManager(context)
            // Remove dividers for better Material Design spacing
        }

        // Set up SwipeRefreshLayout
        refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_layout).apply {
            setOnRefreshListener {
                refreshInbox()
            }
        }

        // Set up empty state
        setupEmptyState()

        fetchMessageFromSdk()
    }

    private fun fetchMessageFromSdk() {
        job = launch {
            // Here we are using coroutines to move the request of Inbox messages from the SDK off of the main thread.
            // This will prevent any visual hiccups in our UI since the SDK will perform the database lookup  of
            // messages on the calling thread.
            val messages = withContext(Dispatchers.Default) {
                mceSdk.inboxMessageManager.messages
            }

            updateSubtitle(messages.size)
            inboxAdapter.messages = messages
                .sortedWith(compareByDescending(nullsFirst<Date>()) { it.sendDateUtc })

            // Show/hide empty state
            updateEmptyState(messages.isEmpty())

            refreshLayout.isRefreshing = false
        }
    }

    private fun refreshInbox() {
        // Request that the SDK retrieve an updated list of Inbox messages from the Marketing Cloud.  To
        // prevent bad behaviors, the SDK will throttle these requests for 1 minute.
        mceSdk.inboxMessageManager.refreshInbox { refreshing ->
            // `refreshing` will be true if the SDK initiated the request to the Marketing Cloud.  If `refreshing`
            // is false then the refresh was throttled.
            if (!refreshing) {
                refreshLayout.isRefreshing = false
                NotificationManager.showInfo("Refresh was throttled. Please wait before trying again.")
            } else {
                NotificationManager.showSuccess("Refreshing inbox messages...")
            }
        }
    }

    private fun setupEmptyState() {
        val view = requireView()
        emptyStateView = view.findViewById(R.id.empty_state)

        emptyStateView.findViewById<View>(R.id.button_refresh_empty).setOnClickListener {
            refreshInbox()
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            refreshLayout.visibility = View.GONE
            emptyStateView.visibility = View.VISIBLE
        } else {
            refreshLayout.visibility = View.VISIBLE
            emptyStateView.visibility = View.GONE
        }
    }

    override fun onInboxMessagesChanged(messages: List<InboxMessage>) {
        // The messages provided in this callback will contain every message sent from the Marketing Cloud, including
        // message that are not yet active.  Instead of filtering out inactive/ineligible messages from this list we
        // will instead request the presentable list of messages from the SDK.
        fetchMessageFromSdk()
    }

    private fun showDeleteConfirmationDialog(message: InboxMessage) {
        val messageTitle = when {
            message.subject != null -> message.subject
            message.title != null -> message.title
            message.alert != null -> message.alert
            else -> "this message"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Message")
            .setMessage("Are you sure you want to delete \"$messageTitle\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMessage(message)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun deleteMessage(message: InboxMessage) {
        MarketingCloudSdk.requestSdk { sdk ->
            // Use the message ID for deletion as requested
            sdk.inboxMessageManager.deleteMessage(message)
            fetchMessageFromSdk()
            NotificationManager.showSuccess("Message deleted")
        }
    }

    private fun updateSubtitle(numOfMessages: Int) {
        if (findNavController().currentDestination?.id == R.id.inbox) {
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
                "Number of messages: $numOfMessages"
        }
    }

    inner class InboxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val subjectView: TextView = view.findViewById(R.id.textview_inbox_subject)
        private val startDateView: TextView = view.findViewById(R.id.textview_inbox_start_date)
        private val readIndicator: View = view.findViewById(R.id.read_indicator)
        private val deleteButton: View = view.findViewById(R.id.button_delete_message)

        fun bind(
            message: InboxMessage,
            clickListener: (InboxMessage) -> Unit,
            longClickListener: (InboxMessage) -> Unit,
            deleteClickListener: (InboxMessage) -> Unit
        ) {
            itemView.setOnClickListener { clickListener.invoke(message) }
            itemView.setOnLongClickListener { longClickListener.invoke(message); true }
            deleteButton.setOnClickListener { deleteClickListener.invoke(message) }

            // When the SDK receives an Alert+Inbox push message from the Marketing Cloud via FCM it will populate a
            // record in the Inbox list based off of the content in the push message.  This is not the complete set of
            // content for the Inbox message and will not contain the subject.  When the Inbox+Push message is received
            // with the app in the foreground the SDK will immediately make a request to retrieve the updated list of
            // Inbox messages from the Marketing Cloud, but that does mean there is the potential for us to display a
            // message whose subject field is `null`.  To prevent an empty item in our RecyclerView we will fall back
            // to the push title or alert message when the subject field is `null`.
            subjectView.text = when {
                message.subject != null -> message.subject
                message.title != null -> message.title
                message.alert != null -> message.alert
                else -> "No subject provided."
            }

            startDateView.text = message.sendDateUtc?.let { dateFormat.format(it) } ?: ""

            // Enhanced read/unread visual treatment
            if (message.read) {
                // Read message styling
                subjectView.typeface = Typeface.DEFAULT
                subjectView.alpha = 0.7f
                startDateView.alpha = 0.7f
                readIndicator.visibility = View.GONE
            } else {
                // Unread message styling
                subjectView.typeface = Typeface.DEFAULT_BOLD
                subjectView.alpha = 1.0f
                startDateView.alpha = 1.0f
                readIndicator.visibility = View.VISIBLE
            }
        }
    }

    inner class InboxAdapter(
        private val messageClickListener: (InboxMessage) -> Unit,
        private val messageLongClickListener: (InboxMessage) -> Unit,
        private val messageDeleteClickListener: (InboxMessage) -> Unit
    ) :
        RecyclerView.Adapter<InboxViewHolder>() {

        var messages: List<InboxMessage> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_inbox, parent, false)
            return InboxViewHolder(view)
        }

        override fun getItemCount(): Int = messages.size

        override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
            val message = messages[position]
            holder.bind(
                message,
                messageClickListener,
                messageLongClickListener,
                messageDeleteClickListener
            )
        }
    }
}