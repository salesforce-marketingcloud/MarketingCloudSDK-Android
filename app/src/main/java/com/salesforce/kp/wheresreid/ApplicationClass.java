
package com.salesforce.kp.wheresreid;

import android.app.Application;
import android.util.Log;

import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETPushConfig;
import com.exacttarget.etpushsdk.data.Attribute;
import com.exacttarget.etpushsdk.event.RegistrationEvent;
import com.exacttarget.etpushsdk.util.EventBus;


/**
 * ApplicationClass is the primary application class.
 * This class extends Application to provide global activities.
 *
 * @author Salesforce (R) 2015.
 *
 */
public class ApplicationClass extends Application {

    private static final String TAG = "ApplicationClass";

     /**
      * ANALYTICS_ENABLED is set to true to show how Salesforce analytics will save statistics for
      * how your customers use the app.
      *
      * CLOUD_PAGES_ENABLED is set to true to test how notifications can send your app customers to
      * different web pages.
      *
      * WAMA_ENABLED is set to true to show how Predictive Intelligence analytics (PIAnalytics) will
      * save statistics for how your customers use the app (by invitation at this point).
      *
      * LOCATION_ENABLED is set to true to show how geo fencing works within the SDK.
      *
      * Your app will have these choices set based on how you want your app to work.
      */

    private static final boolean ANALYTICS_ENABLED = true;
    private static final boolean CLOUD_PAGES_ENABLED = true;
    private static final boolean WAMA_ENABLED = true;
    private static final boolean LOCATION_ENABLED = true;

    /**
     * In ETPush.readyAimFire() you must set several parameters.
     * AppId and AccessToken: these values are taken from the Marketing Cloud definition for your app.
     * GcmSenderId for the push notifications: this value is taken from the Google API console.
     * You also set whether you enable LocationManager, CloudPages, and Analytics.
     *
     * When ReadyAimFire() is called for the first time for a device, it will get a device token
     * from Google or Amazon and send to the MarketingCloud.
     *
     * As well, the ETPackageReplacedReceiver will ensure that a new device token is retrieved from
     * Google or Amazon when a new version of your app is installed.  However, it will only initiate
     * the send when the user opens the app and your app calls readyAimFire().
     *
     * To set the logging level, call ETPush.setLogLevel().
     *
     * The application keys are stored in a separate file (secrets.xml) in order to provide
     * centralized access to these keys and to ensure you use the appropriate keys when
     * compiling the test and production versions.  This is not part of the SDK, but is shown
     * as a way to assist in managing these keys.
     *
    **/

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Register the application to listen for events posted to a private communication bus
         * by the SDK.
         */
        EventBus.getInstance().register(this);

        /** Register to receive push notifications. */
        try {
            ETPush.readyAimFire(new ETPushConfig.Builder(this)
                            .setEtAppId(getString(R.string.app_id))
                            .setAccessToken(getString(R.string.access_token))
                            .setGcmSenderId(getString(R.string.gcm_sender_id))
                            .setLogLevel(BuildConfig.DEBUG ? Log.VERBOSE : Log.ERROR)
                            .setAnalyticsEnabled(ANALYTICS_ENABLED)
                            .setLocationEnabled(LOCATION_ENABLED)
                            .setPiAnalyticsEnabled(WAMA_ENABLED)
                            .setCloudPagesEnabled(CLOUD_PAGES_ENABLED)
                            .build()
            );
        } catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Listens for a RegistrationEvent on EventBus callback.
     *
     * This method is one of several methods to log notifications when an event occurs in the SDK.
     * Different attributes indicate which event has occurred.
     *
     * RegistrationEvent will be triggered when the SDK receives the response from the
     * registration as triggered by the com.google.android.c2dm.intent.REGISTRATION intent.
     *
     * These events are only called if EventBus.getInstance().register() is called.
     *
     * @param event the type of event we're listening for.
     */

    @SuppressWarnings("unused")
    public void onEvent(final RegistrationEvent event) {
        if (ETPush.getLogLevel() <= Log.DEBUG) {
            Log.d(TAG, "Marketing Cloud update occurred.");
            Log.d(TAG, "Device ID:" + event.getDeviceId());
            Log.d(TAG, "Device Token:" + event.getSystemToken());
            Log.d(TAG, "Subscriber key:" + event.getSubscriberKey());
            for (Object attribute : event.getAttributes()) {
                Log.d(TAG, "Attribute " + ((Attribute) attribute).getKey() + ": [" + ((Attribute) attribute).getValue() + "]");
            }
            Log.d(TAG, "Tags: " + event.getTags());
            Log.d(TAG, "Language: " + event.getLocale());
            Log.d(TAG, String.format("Last sent: %1$d", System.currentTimeMillis()));
        }
    }
}

        