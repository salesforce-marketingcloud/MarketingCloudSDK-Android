package com.salesforce.kp.wheresreid;

import java.util.ArrayList;

/**
 * McLocationManager class manages the geofence and beacon location messages.
 *
 * @author Salesforce &reg; 2015.
 *
 */
public class McLocationManager {

    private static McLocationManager ourInstance = null;

    /**
     *  Geolocations retrieved from Marketing cloud's SDK
     */
    private ArrayList<McGeofence> geofences;
    /**
     * Beacons retrieved from Marketing cloud's SDK
     */
    private ArrayList<McBeacon> beacons;

    public static McLocationManager getInstance() {
        if (ourInstance == null){
            ourInstance = new McLocationManager();
            ourInstance.setGeofences(new ArrayList<McGeofence>());
            ourInstance.setBeacons(new ArrayList<McBeacon>());
        }
        return ourInstance;
    }

    public ArrayList<McGeofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(ArrayList<McGeofence> geofences) {
        this.geofences = geofences;
    }

    public ArrayList<McBeacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<McBeacon> beacons) {
        this.beacons = beacons;
    }
}
