package com.salesforce.marketingcloud.learningapp

import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions

class LearningApplication : BaseLearningApplication() {

    override val marketingCloudConfig: MarketingCloudConfig
        get() = MarketingCloudConfig.builder().apply {
            applyCommonConfig()
            setSenderId(BuildConfig.MC_SENDER_ID)
            setNotificationCustomizationOptions(NotificationCustomizationOptions.create(R.drawable.ic_notification))
        }.build(this)
}