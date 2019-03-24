package com.test.shyam.trackie;

import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng CurrLoc;
    private LatLng DestLoc;
    private Button currLoc, destLoc, navigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currLoc = findViewById(R.id.curr_loc);
        destLoc = findViewById(R.id.dest_loc);
        navigate = findViewById(R.id.map_navi);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition curr = new CameraPosition.Builder()
                        .target(CurrLoc)
                        .tilt(45)
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(curr));
            }
        });
        destLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition curr = new CameraPosition.Builder()
                        .target(DestLoc)
                        .tilt(90)
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(curr));
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dest = String.valueOf(DestLoc.latitude) + "," + String.valueOf(DestLoc.longitude);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?daddr=" + dest)));
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String[] loc = getIntent().getStringExtra("Loc").split(",");
        DestLoc = new LatLng(Double.valueOf(loc[0]) , Double.valueOf(loc[1]));
        mMap.addMarker(new MarkerOptions().position(DestLoc).title("Required Location")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DestLoc, 15));
        GPSTracker tracker = new GPSTracker(getApplicationContext());
        CurrLoc = new LatLng(tracker.getLatitude(), tracker.getLongitude());
        //CurrLoc = new LatLng(13.077172, 80.185658);
        mMap.addMarker(new MarkerOptions().position(CurrLoc).title("Current Location"));
    }
}
