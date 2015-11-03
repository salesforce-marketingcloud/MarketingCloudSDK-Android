package com.salesforce.kp.wheresreid;

import com.google.android.gms.maps.model.LatLng;

/**
 * Object meant to store Geolocations's data
 *
 * Created by admin on 10/26/15.
 */
public class McLocation {

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
