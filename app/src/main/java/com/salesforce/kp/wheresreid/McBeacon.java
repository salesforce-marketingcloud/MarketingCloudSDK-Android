package com.salesforce.kp.wheresreid;

import com.google.android.gms.maps.model.LatLng;


/**
 * McBeacon class stores beacon message data.
 *
 * @author Salesforce &reg; 2015.
 */
public class McBeacon {

    private String guid;
    private String name;
    private LatLng coordenates;
    private int radius;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
