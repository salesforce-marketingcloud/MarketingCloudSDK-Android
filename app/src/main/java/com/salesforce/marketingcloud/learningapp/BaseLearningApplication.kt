package com.salesforce.marketingcloud.learningapp

import android.app.Application
import com.salesforce.marketingcloud.MCLogListener
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.MarketingCloudSdk

abstract class BaseLearningApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            MarketingCloudSdk.setLogLevel(MCLogListener.VERBOSE)
            MarketingCloudSdk.setLogListener(MCLogListener.AndroidLogListener())
        }

        // You MUST initialize the SDK in your Application's onCreate to ensure correct
        // functionality when the app is launched from a background service (receiving push message,
        // entering a geofence, ...)
        MarketingCloudSdk.init(this, marketingCloudConfig) { initStatus ->

        }
    }

    abstract val marketingCloudConfig: MarketingCloudConfig

    internal fun MarketingCloudConfig.Builder.applyCommonConfig() {
        setApplicationId(BuildConfig.MC_ACCESS_TOKEN)
        setAccessToken(BuildConfig.MC_ACCESS_TOKEN)
        setMid(BuildConfig.MC_MID)
        setMarketingCloudServerUrl(BuildConfig.MC_SERVER_URL)
        setInboxEnabled(true)
        setAnalyticsEnabled(true)
        setPiAnalyticsEnabled(true)
        setGeofencingEnabled(true)
        setProximityEnabled(true)
    }
}