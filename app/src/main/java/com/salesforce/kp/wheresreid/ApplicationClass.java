
package com.salesforce.kp.wheresreid;

import android.app.Application;
import android.util.Log;

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
 * @author Salesforce (R) 2015.
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
      * PROXIMITY_ENABLED is set to true to show how beacons messages works within the SDK.
      *
      * LOCATION_ENABLED is set to true to show how geo fencing works within the SDK.
      *
      * Your app will have these choices set based on how you want your app to work.
      */
    public static final boolean ANALYTICS_ENABLED = true;
    public static final boolean CLOUD_PAGES_ENABLED = true;
    public static final boolean WAMA_ENABLED = true;
    public static final boolean PROXIMITY_ENABLED = true;
    public static final boolean LOCATION_ENABLED = true;

    /**
     * In ETPush.readyAimFire() you must set several parameters.
     * AppId and AccessToken: these values are taken from the Marketing Cloud definition for your app.
     * GcmSenderId for the push notifications: this value is taken from the Google API console.
     * You also set whether you enable LocationManager, CloudPages, and Analytics.
     *
     * When ReadyAimFire() is called for the first time for a device, it will get a device token
     * from Google and send to the MarketingCloud.
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
                            .setProximityEnabled(PROXIMITY_ENABLED)
                            .build()
            );
        } catch (ETException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void onEvent (final ReadyAimFireInitCompletedEvent event){
        try {
            ETLocationManager.getInstance().startWatchingProximity();
        } catch (ETException e) {}
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
     * @param event contains attributes which identify the type of event and are logged.
     */
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

    /**
     * Listens for a GeofenceResponseEvent on EventBus callback.
     *
     * This event retrieves the data related to geolocations
     * beacons are saved as a list of McLocation in McLocationManager
     *
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused, unchecked")
    public void onEvent(final GeofenceResponseEvent event) {
        ArrayList<Region> regions = (ArrayList<Region>) event.getFences();
        for (Region r : regions){
            McLocation newLocation = new McLocation();
            LatLng latLng = new LatLng(r.getLatitude(), r.getLongitude());
            newLocation.setCoordenates(latLng);
            newLocation.setRadius(r.getRadius());
            newLocation.setName(r.getName());
            McLocationManager.getInstance().getLocations().add(newLocation);
        }
    }

    /**
     * Listens for a BeaconResponseEvent on EventBus callback.
     *
     * This event retrieves the data related to beacons,
     * beacons are saved as a list of McBeacon in McLocationManager
     *
     * @param event the type of event we're listening for.
     */
    @SuppressWarnings("unused, unchecked")
    public void onEvent(final BeaconResponseEvent event) {
        ArrayList<Region> regions = (ArrayList<Region>) event.getBeacons();
        for (Region r : regions){
            McBeacon newBeacon = new McBeacon();
            LatLng latLng = new LatLng(r.getLatitude(), r.getLongitude());
            newBeacon.setCoordenates(latLng);
            newBeacon.setRadius(getResources().getInteger(R.integer.beacon_radius));
            newBeacon.setName(r.getName());
            newBeacon.setGuid(r.getGuid());
            McLocationManager.getInstance().getBeacons().add(newBeacon);
        }
    }
}
