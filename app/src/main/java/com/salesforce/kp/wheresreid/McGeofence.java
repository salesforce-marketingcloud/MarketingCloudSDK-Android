package com.salesforce.kp.wheresreid;

import com.google.android.gms.maps.model.LatLng;

/**
 * MCGeofence class stores geolocation message data.
 *
 * @author Salesforce &reg; 2015.
 */
public class MCGeofence {

    private LatLng coordenates;
    private int radius;
    private String name;

    public LatLng getCoordenates() {
        return coordenates;
    }

    public void setCoordenates(LatLng coordenates) {
        this.coordenates = coordenates;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
