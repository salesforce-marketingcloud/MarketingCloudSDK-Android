/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.marketingcloud.android.demoapp;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.exacttarget.etpushsdk.ETAnalytics;
import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETLogListener;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETPushConfig;
import com.exacttarget.etpushsdk.ETPushConfigureSdkListener;
import com.exacttarget.etpushsdk.ETRequestStatus;
import com.exacttarget.etpushsdk.data.Attribute;
import com.exacttarget.etpushsdk.data.Region;
import com.exacttarget.etpushsdk.event.BeaconResponseEvent;
import com.exacttarget.etpushsdk.event.GeofenceResponseEvent;
import com.exacttarget.etpushsdk.event.RegistrationEvent;
import com.exacttarget.etpushsdk.util.EventBus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedHashSet;


/**
 * ApplicationClass is the primary application class.
 * This class extends Application to provide global activities.
 * <p/>
 * As of 2016-02, you can now implement the ETLogListener interface.
 * Doing so enables you to capture log output from the SDK programmatically.
 *
 * @author Salesforce &reg; 2015.
 */
public class ApplicationClass extends Application implements ETLogListener, ETPushConfigureSdkListener {

    /**
     * Set to true to show how Salesforce analytics will save statistics for
     * how your customers use the app.
     */
    public static final boolean ANALYTICS_ENABLED = true;
    /**
     * Set to true to test how notifications can send your app customers to
     * different web pages.
     */
    public static final boolean CLOUD_PAGES_ENABLED = true;
    /**
     * Set to true to show how Predictive Intelligence analytics (PIAnalytics) will
     * save statistics for how your customers use the app (by invitation at this point).
     */
    public static final boolean WAMA_ENABLED = true;
    /**
     * Set to true to show how beacons messages works within the SDK.
     */
    public static final boolean PROXIMITY_ENABLED = true;
    /**
     * Set to true to show how geo fencing works within the SDK.
     */
    public static final boolean LOCATION_ENABLED = true;
    private static final String TAG = "ApplicationClass";
    private static final LinkedHashSet<EtPushListener> listeners = new LinkedHashSet<>();
    private static ETPush etPush;

    /**
     * If ETPush is null then hold on to a reference of the listener so we can notify them when push
     * is ready.
     *
     * @param etPushListener our object that cares about ETPush
     * @return ETPush or null
     */
    public static ETPush getEtPush(@NonNull final EtPushListener etPushListener) {
        if (etPush == null) {
            listeners.add(etPushListener);
        }
        return etPush;
    }

    /**
     * The onCreate() method initialize your app.
     * <p/>
     * It registers the application to listen for events posted to a private communication bus
     * by the SDK and calls `ETPush.readyAimFire` to configures the SDK to point to the correct code
     * application and to initialize the ETPush, according to the constants defined before.
     * <p/>
     * When ReadyAimFire() is called for the first time for a device, it will get a device token
     * from Google and send to the MarketingCloud.
     * <p/>
     * In ETPush.readyAimFire() you must set several parameters:
     * <ul>
     * <li>
     * AppId and AccessToken: these values are taken from the Marketing Cloud definition for your app.
     * </li>
     * <li>
     * GcmSenderId for the push notifications: this value is taken from the Google API console.
     * </li>
     * <li>
     * You also set whether you enable LocationManager, CloudPages, and Analytics.
     * </li>
     * </ul>
     * <p/>
     * <p/>
     * The application keys are stored in a separate file (secrets.xml) in order to provide
     * centralized access to these keys and to ensure you use the appropriate keys when
     * compiling the test and production versions.
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
            ETPush.configureSdk(new ETPushConfig.Builder(this)
                            .setEtAppId(getString(R.string.app_id))
                            .setAccessToken(getString(R.string.access_token))
                            .setGcmSenderId(getString(R.string.gcm_sender_id))
                            .setLogLevel(BuildConfig.DEBUG ? Log.VERBOSE : Log.ERROR)
                            .setAnalyticsEnabled(ANALYTICS_ENABLED)
                            .setLocationEnabled(LOCATION_ENABLED)
                            .setPiAnalyticsEnabled(WAMA_ENABLED)
                            .setCloudPagesEnabled(CLOUD_PAGES_ENABLED)
                            .setProximityEnabled(PROXIMITY_ENABLED)
                            .build()
                    , this // Our ETPushConfigureSdkListener
            );
        } catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Called when configureSdk() has successfully completed.
     * <p/>
     * When the readyAimFire() initialization is completed, start watching at beacon messages.
     *
     * @param etPush          a ready-to-use instance of ETPush.
     * @param etRequestStatus an additional status field regarding SDK readiness.
     */
    @Override
    public void onETPushConfigurationSuccess(final ETPush etPush, final ETRequestStatus etRequestStatus) {
        ApplicationClass.etPush = etPush;
        ETAnalytics.trackPageView("data://ReadyAimFireCompleted", "Marketing Cloud SDK Initialization Complete");

        // If there was an user recoverable issue with Google Play Services then show a notification to the user
        int googlePlayServicesStatus = etRequestStatus.getGooglePlayServiceStatusCode();
        if (googlePlayServicesStatus != ConnectionResult.SUCCESS && GoogleApiAvailability.getInstance().isUserResolvableError(googlePlayServicesStatus)) {
            GoogleApiAvailability.getInstance().showErrorNotification(this, googlePlayServicesStatus);
        }

        String sdkState;
        try {
            sdkState = ETPush.getInstance().getSDKState();
        } catch (ETException e) {
            sdkState = e.getMessage();
        }
        Log.v(TAG, sdkState); // Write the current SDK State to the Logs.

        if (!listeners.isEmpty()) { // Tell our listeners that the SDK is ready for use
            for (EtPushListener listener : listeners) {
                if (listener != null) {
                    listener.onReadyForPush(etPush);
                }
            }
            listeners.clear();
        }
    }

    /**
     * Called when the SDK failed to initialized.
     *
     * @param etException an exception containing the reason/message regarding the failure.
     */
    @Override
    public void onETPushConfigurationFailed(ETException etException) {
        Log.e(TAG, etException.getMessage(), etException);
    }

    /**
     * Listens for a RegistrationEvent on EventBus callback.
     * <p/>
     * This method is one of several methods to log notifications when an event occurs in the SDK.
     * Different attributes indicate which event has occurred.
     * <p/>
     * RegistrationEvent will be triggered when the SDK receives the response from the
     * registration as triggered by the com.google.android.c2dm.intent.REGISTRATION intent.
     * <p/>
     * These events are only called if EventBus.getInstance().register() is called.
     * <p/>
     *
     * @param event contains attributes which identify the type of event and are logged.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public void onEvent(final RegistrationEvent event) {
        ETAnalytics.trackPageView("data://RegistrationEvent", "Registration Event Completed");
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

    /**
     * Listens for a GeofenceResponseEvent on EventBus callback.
     * <p/>
     * This event retrieves the data related to geolocations
     * beacons are saved as a list of MCGeofence in MCLocationManager
     *
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused, unchecked")
    public void onEvent(final GeofenceResponseEvent event) {
        ETAnalytics.trackPageView("data://GeofenceResponseEvent", "Geofence Response Event Received");
        ArrayList<Region> regions = (ArrayList<Region>) event.getFences();
        for (Region r : regions) {
            MCGeofence newLocation = new MCGeofence();
            LatLng latLng = new LatLng(r.getLatitude(), r.getLongitude());
            newLocation.setCoordenates(latLng);
            newLocation.setRadius(r.getRadius());
            newLocation.setName(r.getName());
            MCLocationManager.getInstance().getGeofences().add(newLocation);
        }
    }

    /**
     * Listens for a BeaconResponseEvent on EventBus callback.
     * <p/>
     * This event retrieves the data related to beacon messages and saves them
     * as a list of MCBeacon in MCLocationManager.
     * <p/>
     *
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused, unchecked")
    public void onEvent(final BeaconResponseEvent event) {
        ETAnalytics.trackPageView("data://BeaconResponse", "Beacon Response Event Received");
        ArrayList<Region> regions = (ArrayList<Region>) event.getBeacons();
        for (Region r : regions) {
            MCBeacon newBeacon = new MCBeacon();
            LatLng latLng = new LatLng(r.getLatitude(), r.getLongitude());
            newBeacon.setCoordenates(latLng);
            newBeacon.setRadius(getResources().getInteger(R.integer.beacon_radius));
            newBeacon.setName(r.getName());
            newBeacon.setGuid(r.getGuid());
            MCLocationManager.getInstance().getBeacons().add(newBeacon);
        }
    }

    @Override
    public void out(int severity, String tag, String message, @Nullable Throwable throwable) {
        /*
         * Using this method you can interact with SDK log output.
         * Severity is populated with log levels like Log.VERBOSE, Log.INFO etc.
         * Message, is populated with the actual log output text.
         * Tag, is a free form string representing the log tag you've selected.
         * Finally, the optional Throwable Throwable represents a thrown exception.
         */

        /*
         * Assuming you have crashytics enabled for your app, the following code would send
         * log data to Crashytics in the event that the log's severity is ERROR or ASSERT
         */

        if (throwable != null) {
            // We have an exception to log:
            // Commenting out all references to Crashlytics.
            // Crashlytics.logException(throwable);
        }

        switch (severity) {
            case Log.ERROR:
                Log.e(tag, message);
                // Crashlytics.log(severity, tag, message);
                break;
            case Log.ASSERT:
                Log.wtf(tag, message);
                // Crashlytics.log(severity, tag, message);
                try {
                    // If we're logging a failed ASSERT, also grab the getSDKState() data and log that as well
                    Log.v("SDKState Information", ETPush.getInstance().getSDKState());
                    // Crashlytics.log(ETPush.getInstance().getSDKState());
                } catch (ETException etException) {
                    Log.v("ErrorGettingSDKState", etException.getMessage());
                    // Crashlytics.log(String.format(Locale.ENGLISH, "Error Getting SDK State: %s", etException.getMessage()));
                }
                break;
            default:
                Log.v(tag, message);
        }
    }

    /*
     * Public interface for the main activity to implement
     */
    public interface EtPushListener {
        void onReadyForPush(ETPush etPush);
    }

}
