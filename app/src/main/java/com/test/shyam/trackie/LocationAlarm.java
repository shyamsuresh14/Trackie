package com.test.shyam.trackie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.function.ToLongBiFunction;

public class LocationAlarm extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currLoc;
    public LatLng destLoc;
    private Marker destMarker;
    private Button setBtn;
    private Switch alarm;
    public boolean alarmStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_alarm);
        setBtn = findViewById(R.id.set_btn);
        alarm = findViewById(R.id.status);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Location Set", Toast.LENGTH_SHORT).show();
            }
        });

        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "Alarm On", Toast.LENGTH_SHORT).show();
                    alarmStatus = true;
                    int repeatTime = 30;  //Repeat alarm time in seconds
                    AlarmManager processTimer = (AlarmManager)getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(LocationAlarm.this, LocationTimer.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(LocationAlarm.this , 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Repeat alarm every second
                    processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),repeatTime*1000, pendingIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Alarm Off", Toast.LENGTH_SHORT).show();
                    alarmStatus = false;
                    Intent intent = new Intent(LocationAlarm.this, LocationTimer.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(LocationAlarm.this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
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
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        currLoc = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLoc).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(destMarker != null)
                    destMarker.remove();

                destLoc = latLng;
                destMarker = mMap.addMarker(new MarkerOptions().position(destLoc).title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        });
    }

}
