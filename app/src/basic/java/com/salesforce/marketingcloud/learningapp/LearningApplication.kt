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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.mobileappmessaging.MobileAppMessagingConfig
import com.salesforce.marketingcloud.pushfeature.config.PushFeatureConfig
import com.salesforce.marketingcloud.pushfeature.push.UrlHandler
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig
import java.util.Random
import com.salesforce.marketingcloud.pushmodels.NotificationMessage as PushNotificationMessage

class LearningApplication : BaseLearningApplication() {

    /**
     * Local abstraction for URL handling functionality.
     * This provides the common implementation for both URL handler interfaces.
     */
    private val urlHandlerImplementation = { context: Context, url: String, urlSource: String ->
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
    private val pushNotificationUrlHandler = { context: Context, message: PushNotificationMessage ->
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

    override val sdkConfigBuilder: SFMCSdkModuleConfig
        get() = SFMCSdkModuleConfig.build {
            engagementModuleConfig = mceConfigBuilder
            mamModuleConfig = mamConfigBuilder
            pushFeatureModuleConfig = pushConfigBuilder
        }

    val mceConfigBuilder: MarketingCloudConfig
        get() = MarketingCloudConfig.builder().apply {
            setApplicationId(BuildConfig.MC_APP_ID)
            setAccessToken(BuildConfig.MC_ACCESS_TOKEN)
            setMid(BuildConfig.MC_MID)
            setMarketingCloudServerUrl(BuildConfig.MC_SERVER_URL)
            setInboxEnabled(true)
            setAnalyticsEnabled(true)
            //setPiAnalyticsEnabled(true)
            //setGeofencingEnabled(true)
            //setProximityEnabled(true)
            //setProximityNotificationOptions(ProximityNotificationCustomizationOptions.create(R.drawable.ic_notification))
            setUrlHandler(urlHandlerImplementation)
        }.build(this)

    val mamConfigBuilder: MobileAppMessagingConfig
        get() = MobileAppMessagingConfig.builder().apply {
            moduleApplicationId(BuildConfig.MAM_APP_ID)
            accessToken(BuildConfig.MAM_ACCESS_TOKEN)
            tenantId(BuildConfig.MAM_TENANT_ID)
            endpointUrl(BuildConfig.MAM_ENDPOINT_URL)
            analyticsEnabled(true)
        }.build()

    val pushConfigBuilder: PushFeatureConfig
        get() = PushFeatureConfig.builder().apply {
            setSenderId(BuildConfig.MC_SENDER_ID)
            setNotificationCustomizationOptions(
                com.salesforce.marketingcloud.pushfeature.notifications.NotificationCustomizationOptions.create(
                    R.drawable.ic_notification, pushNotificationUrlHandler, null /* use default */
                )
            )
            setUrlHandler(urlHandlerImplementation)
        }.build()
}