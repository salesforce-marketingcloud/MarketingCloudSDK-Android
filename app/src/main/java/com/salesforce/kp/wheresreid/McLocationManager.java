package com.salesforce.kp.wheresreid;

import java.util.ArrayList;

/**
 * Created by admin on 10/26/15.
 */
public class McLocationManager {

    private static McLocationManager ourInstance = null;
    /* Geolocations retrieved from Marketing cloud's SDK */
    private ArrayList<McLocation> locations;
    /* Beacons retrieved from Marketing cloud's SDK */
    private ArrayList<McBeacon> beacons;

    public static McLocationManager getInstance() {
        if (ourInstance == null){
            ourInstance = new McLocationManager();
            ourInstance.setLocations(new ArrayList<McLocation>());
            ourInstance.setBeacons(new ArrayList<McBeacon>());
        }
        return ourInstance;
    }

    private McLocationManager() {
    }

    public ArrayList<McLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<McLocation> locations) {
        this.locations = locations;
    }

    public ArrayList<McBeacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<McBeacon> beacons) {
        this.beacons = beacons;
    }
}
