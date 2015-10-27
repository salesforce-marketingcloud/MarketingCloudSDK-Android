package com.salesforce.kp.wheresreid;

import java.util.ArrayList;

/**
 * Created by admin on 10/26/15.
 */
public class McLocationManager {

    private static McLocationManager ourInstance = null;
    private ArrayList<McLocation> locations;

    public static McLocationManager getInstance() {
        if (ourInstance == null){
            ourInstance = new McLocationManager();
            ourInstance.setLocations(new ArrayList<McLocation>());
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
}
