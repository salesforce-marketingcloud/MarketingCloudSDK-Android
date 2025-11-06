package com.salesforce.marketingcloud.learningapp

import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions
import com.salesforce.marketingcloud.pushfeature.config.PushFeatureConfig
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdkModuleConfig

class LearningApplication : BaseLearningApplication() {
    override val sdkConfigBuilder: SFMCSdkModuleConfig
        get() = SFMCSdkModuleConfig.build {
            engagementModuleConfig = MarketingCloudConfig.builder().apply {
                setApplicationId(BuildConfig.MC_APP_ID)
                setAccessToken(BuildConfig.MC_ACCESS_TOKEN)
                setMid(BuildConfig.MC_MID)
                setMarketingCloudServerUrl(BuildConfig.MC_SERVER_URL)
                setInboxEnabled(true)
                setAnalyticsEnabled(true)
                //setPiAnalyticsEnabled(true)
                //setGeofencingEnabled(true)
                //setProximityEnabled(true)
            }.build(this@LearningApplication)
        }
}