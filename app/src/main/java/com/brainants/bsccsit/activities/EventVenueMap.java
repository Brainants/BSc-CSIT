package com.brainants.bsccsit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.brainants.bsccsit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventVenueMap extends AppCompatActivity implements OnMapReadyCallback {

    String place;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_venue_map);

        place = getIntent().getStringExtra("name");
        latitude = getIntent().getDoubleExtra("lat", 0.0);
        longitude = getIntent().getDoubleExtra("long", 0.0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng map = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map, 13));

        googleMap.addMarker(new MarkerOptions()
                .title(place)
                .position(map));

    }
}
