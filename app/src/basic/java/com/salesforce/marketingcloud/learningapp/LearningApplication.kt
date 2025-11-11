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

import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.mobileappmessaging.MobileAppMessagingConfig
import com.salesforce.marketingcloud.pushfeature.config.PushFeatureConfig
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig

class LearningApplication : BaseLearningApplication() {


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