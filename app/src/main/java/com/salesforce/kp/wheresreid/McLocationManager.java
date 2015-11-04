package com.salesforce.kp.wheresreid;

import java.util.ArrayList;

/**
 * MCLocationManager class manages the geofence and beacon location messages.
 *
 * @author Salesforce &reg; 2015.
 *
 */
public class MCLocationManager {

    private static MCLocationManager ourInstance = null;

    /**
     *  Geolocations retrieved from Marketing cloud's SDK
     */
    private ArrayList<MCGeofence> geofences;
    /**
     * Beacons retrieved from Marketing cloud's SDK
     */
    private ArrayList<MCBeacon> beacons;

    public static MCLocationManager getInstance() {
        if (ourInstance == null){
            ourInstance = new MCLocationManager();
            ourInstance.setGeofences(new ArrayList<MCGeofence>());
            ourInstance.setBeacons(new ArrayList<MCBeacon>());
        }
        return ourInstance;
    }

    public ArrayList<MCGeofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(ArrayList<MCGeofence> geofences) {
        this.geofences = geofences;
    }

    public ArrayList<MCBeacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<MCBeacon> beacons) {
        this.beacons = beacons;
    }
}
