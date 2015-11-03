package com.salesforce.kp.wheresreid;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * MapsActivity displays a Google map with the regions configured at Marketing Cloud.
 *
 * @author Salesforce &reg; 2015.
 */
public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        setUpMap(map);
    }

    /**
     * Configures the google map
     * In case there is geolocations or beacons
     * @param map Google map to work on
     */
    private void setUpMap(GoogleMap map){
        McLocationManager lm = McLocationManager.getInstance();

        /* lastCoord is the location which the map will show, the default being San Francisco */
        LatLng lastCoord = new LatLng(Double.parseDouble(getResources().getString(R.string.default_latitude)),
                Double.parseDouble(getResources().getString(R.string.default_longitude)));

        /* Loops through the beacons and set them in the map */
        for (McBeacon beacon : lm.getBeacons()){
            map.addMarker(new MarkerOptions()
                    .position(beacon.getCoordenates())
                    .title(beacon.getName())
                    .icon(BitmapDescriptorFactory.fromResource((R.drawable.beacon))));
            map.addCircle(new CircleOptions()
                    .center(beacon.getCoordenates())
                    .radius(beacon.getRadius())
                    .strokeColor(getResources().getColor(R.color.beaconOuterCircle))
                    .fillColor(getResources().getColor(R.color.beaconInnerCircle)));
            lastCoord = beacon.getCoordenates();
        }

        /* Loops through the locations and set them in the map */
        for (McGeofence location : lm.getGeofences()){
            map.addMarker(new MarkerOptions().position(location.getCoordenates()).title(location.getName()));
            map.addCircle(new CircleOptions()
                    .center(location.getCoordenates())
                    .radius(location.getRadius())
                    .strokeColor(getResources().getColor(R.color.geoLocationOuterCircle))
                    .fillColor(getResources().getColor(R.color.geoLocationInnerCircle)));
            lastCoord = location.getCoordenates();
        }
        /* Centers the map in the last coordenate found and sets the zoom */
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lastCoord).zoom(getResources().getInteger(R.integer.map_zoom)).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    /**
     * Navigates back to parent's Activity: MainActivity
     *
     * @param item which is the reference to the parent's activity: MainActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
