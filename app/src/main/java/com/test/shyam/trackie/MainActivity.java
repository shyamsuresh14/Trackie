package com.test.shyam.trackie;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private final int SMS_PERMISSION_CODE = 100;
    //private SmsBroadcastReciever mReceiver;
    private String mNumber;
    private EditText mName, mMobile_no;
    private FloatingActionButton mFab;
    private android.support.v7.widget.Toolbar mToolbar;
    private String TRACK = "Friends", REQ = "requests", TRACE = "Trace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.track_button);
        mName = findViewById(R.id.Name);
        mMobile_no = findViewById(R.id.Mobile_no);
        mFab = findViewById(R.id.Add_button);
        mToolbar = findViewById(R.id.main_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        if(!isSmsPermissionGranted()){
            showRequestPermissionsInfoAlertDialog(true);
        }

        mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == 100 || actionId == EditorInfo.IME_NULL){
                    track();
                    return true;
                }
                return false;
            }
        });

        mMobile_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == 100 || actionId == EditorInfo.IME_NULL){
                    track();
                    return true;
                }
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                track();
                //startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SendRequest.class);
                startActivity(intent);
            }
        });

        if(!isServiceRunning(ReceiverService.class))
            startService(new Intent(this,ReceiverService.class));

        //Service service = new ReceiverService();
        //startForegroundService(service);
        //Log.d("Trackie", "Service started");
        //SmsBroadcastReciever mReciever = new SmsBroadcastReciever(mNumber);
        //registerReceiver(mReciever, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        //test();
    }

    public void track(){
        if (!mName.getText().toString().equals("")) {
            int found = 0;
            SharedPreferences pref = getSharedPreferences(TRACE, 0);
            Set<String> friends = pref.getStringSet(TRACE, new HashSet<String>());

            for(String friend: friends){
                if(friend.split(",")[0].equals(mName.getText().toString())){
                    mNumber = friend.split(",")[1];
                    found = 1;
                    break;
                }
            }
            if(found == 1)
                sendTrackRequest();
            else
                Toast.makeText(getApplicationContext(), "Enter a friend's name", Toast.LENGTH_SHORT).show();

        }
        else if(!mMobile_no.getText().toString().equals("")){
            mNumber = mMobile_no.getText().toString();
            sendTrackRequest();
        }
        else{
            Toast.makeText(getApplicationContext(), "Enter name or mobile no. first!",Toast.LENGTH_SHORT).show();
        }
    }

    public void sendTrackRequest() {
        Log.d("Trackie", "Number:" + mNumber);
        try {
            SmsManager.getDefault().sendTextMessage(mNumber, null, "loc?", null, null);
        } catch (Exception e) {
            Log.d("Trackie", e.toString());
            Toast.makeText(getApplicationContext(),"Mobile No. entered is invalid! + '" + mNumber + "'", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isServiceRunning(Class<?> className){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE))
            if(serviceInfo.service.getClassName().equals(className.getName()))
                return true;
        return false;
    }

    public boolean isSmsPermissionGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request runtime SMS permission
     */
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS}, SMS_PERMISSION_CODE);
    }

    public void showRequestPermissionsInfoAlertDialog(final boolean makeSystemRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Requires permission to recieve,read,send SMS,get contact info and access location"); // Your own title
        builder.setMessage("The App requires this permission to request and get the location of the other user"); // Your own message

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Display system runtime permission request?
                if (makeSystemRequest) {
                    requestPermissions();
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.stop_item)
            stopService(new Intent(this, ReceiverService.class));
        else if(item.getItemId() == R.id.start_item)
            startService(new Intent(this, ReceiverService.class));

        else if(item.getItemId() == R.id.manage_item)
        {
            Intent intent = new Intent(this , FriendsList.class);
            startActivity(intent);
        }

        else if(item.getItemId() == R.id.traceable_item){
            Intent intent = new Intent(this, TraceablesList.class);
            startActivity(intent);
        }

        else if(item.getItemId() == R.id.loc_alarm){
            Intent intent = new Intent(this, LocationAlarm.class);
            startActivity(intent);
        }

        else if(item.getItemId() == R.id.reset_item)
        {
            SharedPreferences pref = getSharedPreferences(TRACK, 0); pref.edit().clear().apply();
            pref = getSharedPreferences(REQ, 0); pref.edit().clear().apply();
            pref = getSharedPreferences(TRACE, 0); pref.edit().clear().apply();
        }

        else if(item.getItemId() == R.id.exit_item)
        {
            finish();
            onDestroy();
        }
        return super.onOptionsItemSelected(item);
    }

    public void test(){
        String TRACK = "Friends"; boolean skip = false;
        SharedPreferences pref = getSharedPreferences(TRACK, 0);
        Set<String> friends = pref.getStringSet(TRACK, new HashSet<String>());
        for(String friend: friends)
        {
            if(friend.equals("7358080328")){
                skip = true;break;
            }
        }
        if(!skip) {
            friends.add("7358080328");
            pref.edit().putStringSet(TRACK, friends).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mReciever);
    }
}
