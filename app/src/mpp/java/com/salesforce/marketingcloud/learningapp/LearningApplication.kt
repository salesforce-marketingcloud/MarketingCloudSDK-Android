package com.salesforce.marketingcloud.learningapp

import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions

class LearningApplication : BaseLearningApplication() {
    override val configBuilder: MarketingCloudConfig.Builder
        get() = MarketingCloudConfig.builder().apply {
            setApplicationId(BuildConfig.MC_APP_ID)
            setAccessToken(BuildConfig.MC_ACCESS_TOKEN)
            // Not setting the sender id signals to the SDK that our code will be handling the push token
            setMid(BuildConfig.MC_MID)
            setMarketingCloudServerUrl(BuildConfig.MC_SERVER_URL)
            setNotificationCustomizationOptions(NotificationCustomizationOptions.create(R.drawable.ic_notification))
            setInboxEnabled(true)
            setAnalyticsEnabled(true)
            setPiAnalyticsEnabled(true)
            setGeofencingEnabled(true)
            setProximityEnabled(true)
            setUrlHandler(this@LearningApplication)
        }

}