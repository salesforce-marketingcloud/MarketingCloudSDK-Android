/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.marketingcloud.android.demoapp;

import android.app.Application;
import android.util.Log;

import com.exacttarget.etpushsdk.ETAnalytics;
import com.exacttarget.etpushsdk.ETException;
import com.exacttarget.etpushsdk.ETLocationManager;
import com.exacttarget.etpushsdk.ETPush;
import com.exacttarget.etpushsdk.ETPushConfig;
import com.exacttarget.etpushsdk.data.Attribute;
import com.exacttarget.etpushsdk.data.Region;
import com.exacttarget.etpushsdk.event.BeaconResponseEvent;
import com.exacttarget.etpushsdk.event.GeofenceResponseEvent;
import com.exacttarget.etpushsdk.event.ReadyAimFireInitCompletedEvent;
import com.exacttarget.etpushsdk.event.RegistrationEvent;
import com.exacttarget.etpushsdk.util.EventBus;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * ApplicationClass is the primary application class.
 * This class extends Application to provide global activities.
 *
 * @author Salesforce &reg; 2015.
 */
public class ApplicationClass extends Application {

    private static final String TAG = "ApplicationClass";

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
     *     <li>
     *         AppId and AccessToken: these values are taken from the Marketing Cloud definition for your app.
     *     </li>
     *     <li>
     *         GcmSenderId for the push notifications: this value is taken from the Google API console.
     *     </li>
     *     <li>
     *         You also set whether you enable LocationManager, CloudPages, and Analytics.
     *     </li>
     * </ul>
     *
     * <p/>
     * The application keys are stored in a separate file (secrets.xml) in order to provide
     * centralized access to these keys and to ensure you use the appropriate keys when
     * compiling the test and production versions.
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
                            .setProximityEnabled(PROXIMITY_ENABLED)
                            .build()
            );
        } catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Listens for a ReadyAimFireInitCompletedEvent on EventBus callback.
     * <p/>
     * When the readyAimFire() initialization is completed, start watching at beacon messages.
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused")
    public void onEvent (final ReadyAimFireInitCompletedEvent event){
        ETAnalytics.trackPageView("data://ReadyAimFireCompleted", "Marketing Cloud SDK Initialization Complete");
        try {
            ETLocationManager.getInstance().startWatchingLocation();
            ETLocationManager.getInstance().startWatchingProximity();
        } catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        }
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
     *
     * This event retrieves the data related to geolocations
     * beacons are saved as a list of MCGeofence in MCLocationManager
     *
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused, unchecked")
    public void onEvent(final GeofenceResponseEvent event) {
        ETAnalytics.trackPageView("data://GeofenceResponseEvent", "Geofence Response Event Received");
        ArrayList<Region> regions = (ArrayList<Region>) event.getFences();
        for (Region r : regions){
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
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused, unchecked")
    public void onEvent(final BeaconResponseEvent event) {
        ETAnalytics.trackPageView("data://BeaconResponse", "Beacon Response Event Received");
        ArrayList<Region> regions = (ArrayList<Region>) event.getBeacons();
        for (Region r : regions){
            MCBeacon newBeacon = new MCBeacon();
            LatLng latLng = new LatLng(r.getLatitude(), r.getLongitude());
            newBeacon.setCoordenates(latLng);
            newBeacon.setRadius(getResources().getInteger(R.integer.beacon_radius));
            newBeacon.setName(r.getName());
            newBeacon.setGuid(r.getGuid());
            MCLocationManager.getInstance().getBeacons().add(newBeacon);
        }
    }
}
