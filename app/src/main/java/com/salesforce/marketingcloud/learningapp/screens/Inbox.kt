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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.learningapp.R
import com.salesforce.marketingcloud.learningapp.SdkFragment
import com.salesforce.marketingcloud.messages.inbox.InboxMessage
import com.salesforce.marketingcloud.messages.inbox.InboxMessageManager
import kotlinx.coroutines.*
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class Inbox : SdkFragment(), CoroutineScope, InboxMessageManager.InboxResponseListener {

    companion object {
        val DATE_FORMAT: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH)
    }

    private lateinit var marketingCloudSdk: MarketingCloudSdk
    private lateinit var refreshLayout: SwipeRefreshLayout

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val inboxAdapter = InboxAdapter(
        messageClickListener = { message ->
            // Mark the message as `read`.
            marketingCloudSdk.inboxMessageManager.setMessageRead(message)

            // Log an open analytics for the Inbox message.
            marketingCloudSdk.analyticsManager.trackInboxOpenEvent(message)

            findNavController().navigate(InboxDirections.actionInboxToInboxViewer(message.url()))
        }, messageLongClickListener = { message ->
            // Mark a message as deleted.  This will remove it from the messages returned from the SDK.
            marketingCloudSdk.inboxMessageManager.deleteMessage(message)

            fetchMessageFromSdk(marketingCloudSdk)
        })

    override val layoutId: Int
        get() = R.layout.fragment_inbox

    override fun onDestroyView() {
        if (::marketingCloudSdk.isInitialized) {
            marketingCloudSdk.inboxMessageManager.unregisterInboxResponseListener(this)
        }
        job.cancel()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (::marketingCloudSdk.isInitialized) {
            fetchMessageFromSdk(marketingCloudSdk)
        }
    }

    override fun onSdkReady(sdk: MarketingCloudSdk) {
        marketingCloudSdk = sdk

        // Here we are registering the Inbox fragment to receive callbacks when the SDK receives a
        // refreshed list of Inbox messages from the Marketing Cloud.
        marketingCloudSdk.inboxMessageManager.registerInboxResponseListener(this)

        val view = requireView()
        view.findViewById<RecyclerView>(R.id.recyclerView_inbox).apply {
            adapter = inboxAdapter
            val llm = LinearLayoutManager(context)
            layoutManager = llm
            addItemDecoration(DividerItemDecoration(context, llm.orientation))

            fetchMessageFromSdk(marketingCloudSdk)
        }

        refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_layout).apply {
            setOnRefreshListener {
                // Request that the SDK retrieve an updated list of Inbox messages from the Marketing Cloud.  To
                // prevent bad behaviors, the SDK will throttle these requests for 1 minute.
                marketingCloudSdk.inboxMessageManager.refreshInbox { refreshing ->
                    // `refreshing` will be true if the SDK initiated the request to the Marketing Cloud.  If `refreshing`
                    // is false then the refresh was throttled.
                    if (!refreshing) isRefreshing = false
                }
            }
        }
    }

    private fun fetchMessageFromSdk(sdk: MarketingCloudSdk) {
        launch {
            // Here we are using coroutines to move the request of Inbox messages from the SDK off of the main thread.
            // This will prevent any visual hiccups in our UI since the SDK will perform the database lookup  of
            // messages on the calling thread.
            val messages = withContext(Dispatchers.Default) {
                sdk.inboxMessageManager.messages
            }

            updateSubtitle(messages.size)
            inboxAdapter.messages = messages
                .sortedWith(compareByDescending(nullsFirst<Date>()) { it.sendDateUtc() })

            refreshLayout.isRefreshing = false
        }
    }

    override fun onInboxMessagesChanged(messages: List<InboxMessage>) {
        // The messages provided in this callback will contain every message sent from the Marketing Cloud, including
        // message that are not yet active.  Instead of filtering out inactive/ineligible messages from this list we
        // will instead request the presentable list of messages from the SDK.
        fetchMessageFromSdk(marketingCloudSdk)
    }

    private fun updateSubtitle(numOfMessages: Int) {
        if (findNavController().currentDestination?.id == R.id.inbox) {
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Number of messages: $numOfMessages"
        }
    }

    inner class InboxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val subjectView: TextView = view.findViewById(R.id.textview_inbox_subject)
        private val startDateView: TextView = view.findViewById(R.id.textview_inbox_start_date)

        fun bind(
            message: InboxMessage,
            clickListener: (InboxMessage) -> Unit,
            longClickListener: (InboxMessage) -> Unit
        ) {
            itemView.setOnClickListener { clickListener.invoke(message) }
            itemView.setOnLongClickListener { longClickListener.invoke(message); true }

            // When the SDK receives an Alert+Inbox push message from the Marketing Cloud via FCM it will populate a
            // record in the Inbox list based off of the content in the push message.  This is not the complete set of
            // content for the Inbox message and will not contain the subject.  When the Inbox+Push message is received
            // with the app in the foreground the SDK will immediately make a request to retrieve the updated list of
            // Inbox messages from the Marketing Cloud, but that does mean there is the potential for us to display a
            // message whose subject field is `null`.  To prevent an empty item in our RecyclerView we will fall back
            // to the push title or alert message when the subject field is `null`.
            subjectView.text = when {
                message.subject() != null -> message.subject()
                message.title() != null -> message.title()
                message.alert() != null -> message.alert()
                else -> "No subject provided."
            }

            startDateView.text = if (message.sendDateUtc() != null) {
                DATE_FORMAT.format(message.sendDateUtc())
            } else ""

            if (message.read()) {
                subjectView.typeface = Typeface.DEFAULT
            } else {
                subjectView.typeface = Typeface.DEFAULT_BOLD
            }
        }
    }

    inner class InboxAdapter(
        private val messageClickListener: (InboxMessage) -> Unit,
        private val messageLongClickListener: (InboxMessage) -> Unit
    ) :
        RecyclerView.Adapter<InboxViewHolder>() {

        var messages: List<InboxMessage> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inbox, parent, false)
            return InboxViewHolder(view)
        }

        override fun getItemCount(): Int = messages.size

        override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
            val message = messages[position]
            holder.bind(message, messageClickListener, messageLongClickListener)
        }
    }
}