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
package com.salesforce.marketingcloud.learningapp

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import com.salesforce.marketingcloud.MCLogListener
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.UrlHandler
import com.salesforce.marketingcloud.inappmessaging.models.InAppMessage
import com.salesforce.marketingcloud.inappmessagingfeature.InAppMessageCloseAction
import com.salesforce.marketingcloud.inappmessagingfeature.InAppMessageManager
import com.salesforce.marketingcloud.inappmessagingfeature.InAppMessagingFeature

import com.salesforce.marketingcloud.sfmcsdk.InitializationStatus
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig
import com.salesforce.marketingcloud.sfmcsdk.components.logging.LogLevel
import com.salesforce.marketingcloud.sfmcsdk.components.logging.LogListener
import java.util.Random
import com.salesforce.marketingcloud.pushmodels.NotificationMessage as PushNotificationMessage

const val LOG_TAG = "~#MCLearningApp"

abstract class BaseLearningApplication : Application() {

    internal abstract val sdkConfigBuilder: SFMCSdkModuleConfig

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // Only log for DEBUG builds
            SFMCSdk.setLogging(LogLevel.DEBUG, LogListener.AndroidLogger())
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())
            SFMCSdk.requestSdk { sdk ->
                Log.i(LOG_TAG, sdk.getSdkState().toString(2)) // Show the SDK State on launch
            }
        }

        // You MUST initialize the SDK in your Application's onCreate to ensure correct
        // functionality when the app is launched from a background service (receiving push message,
        // entering a geofence, ...)
        SFMCSdk.configure(applicationContext as Application, sdkConfigBuilder) { initStatus ->
            when (initStatus.status) {
                InitializationStatus.SUCCESS -> {
                    Log.v(LOG_TAG, "Marketing Cloud initialization successful.")
                }

                InitializationStatus.FAILURE -> {
                    // Given that this app is used to show SDK functionality we will hard exit if SDK init outright failed.
                    Log.e(
                        LOG_TAG,
                        "Marketing Cloud initialization failed.  Exiting Learning App with exception."
                    )
                    throw RuntimeException("Init failed")

                }
            }
        }

        InAppMessagingFeature.requestSdk { it ->

            it.getInAppMessageManager().run {

                // Set the status bar color to be used when displaying an In App Message.
                setStatusBarColor(
                    ContextCompat.getColor(
                        this@BaseLearningApplication,
                        R.color.primaryColor
                    )
                )
                // Set the default font to be used when an In App Message is rendered by the SDK
                setTypeface(
                    ResourcesCompat.getFont(
                        this@BaseLearningApplication,
                        R.font.fira_sans
                    )
                )

                setInAppMessageListener(object : InAppMessageManager.EventListener {
                    override fun shouldShowMessage(message: InAppMessage): Boolean {
                        // This method will be called before an in-app message is presented.
                        // Return false to prevent display; call InAppMessageManager#showMessage
                        // later to display the message if it is still on the device and active.
                        if (IamState.suppressMessages) {
                            val suppressedId = message.id
                            IamState.suppressedMessageId = suppressedId
                            IamState.suppressMessages = false // one-shot: auto-reset after blocking
                            Log.v(LOG_TAG, "$suppressedId was suppressed by IamState flag.")
                            IamState.suppressedIdLiveData.postValue(suppressedId)
                            return false
                        }
                        return true
                    }

                    override fun didShowMessage(message: InAppMessage) {
                        Log.v(LOG_TAG, "${message.id} was displayed.")
                        IamState.lastEvent =
                            "SHOWN  [${message.id}]  type=${message.type}  priority=${message.priority}"
                    }

                    override fun didCloseMessage(
                        message: InAppMessage,
                        action: InAppMessageCloseAction
                    ) {
                        Log.v(LOG_TAG, "${message.id} was closed with action $action")
                        val reason = action.actionType.name
                        val actionId = action.id?.let { "  actionId=$it" } ?: ""
                        IamState.lastEvent =
                            "CLOSED [${message.id}]  reason=$reason$actionId"
                    }
                })
            }

        }
    }


    /**
     * Local abstraction for URL handling functionality.
     * This provides the common implementation for both URL handler interfaces.
     */
     val urlHandlerImplementation = { context: Context, url: String, urlSource: String ->
        Log.d("LearningApplication:urlHandlerImplementation ", "$urlSource, $url")
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        when (urlSource) {
            UrlHandler.DEEPLINK ->
                PendingIntent.getActivity(
                    context,
                    Random().nextInt(),
                    if (intent.resolveActivity(context.packageManager) != null)
                        intent
                    else
                        context.packageManager.getLaunchIntentForPackage(context.packageName),
                    provideIntentFlags()
                )

            in listOf(UrlHandler.URL, UrlHandler.CLOUD_PAGE, UrlHandler.ACTION) ->
                PendingIntent.getActivity(
                    context,
                    Random().nextInt(),
                    intent,
                    provideIntentFlags()
                )

            UrlHandler.APP_OPEN ->
                PendingIntent.getActivity(
                    context,
                    Random().nextInt(),
                    context.packageManager.getLaunchIntentForPackage(context.packageName),
                    provideIntentFlags()
                )

            else -> null // No intent
        }
    }

    private fun provideIntentFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else
            PendingIntent.FLAG_UPDATE_CURRENT
    }

    /**
     * Adapter function to bridge between PushNotificationMessage URL handler and our common implementation
     */
     val pushNotificationUrlHandler = { context: Context, message: PushNotificationMessage ->
        val url = message.url
        if (url.isNullOrBlank()) {
            PendingIntent.getActivity(
                context,
                Random().nextInt(),
                context.packageManager.getLaunchIntentForPackage(context.packageName),
                provideIntentFlags()
            )
        } else {
            PendingIntent.getActivity(
                context,
                Random().nextInt(),
                Intent(Intent.ACTION_VIEW, url.toUri()),
                provideIntentFlags()
            )

        }


    }

}
