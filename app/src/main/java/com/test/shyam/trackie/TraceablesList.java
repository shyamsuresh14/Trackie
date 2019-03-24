package com.test.shyam.trackie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TraceablesList extends AppCompatActivity {

    private Toolbar mToolbar;
    private FriendsAdapter mArrayAdapter;
    //private FloatingActionButton clear;
    private ListView traceablesList;
    private String TRACE = "Trace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traceables_list);

        mToolbar = findViewById(R.id.friends_toolbar);
        //clear = findViewById(R.id.clearBtn);
        getFriends();

        traceablesList = findViewById(R.id.friends_list);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.traceable);

        traceablesList.setAdapter(mArrayAdapter);

        /*clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });*/
    }

    private void getFriends(){
        ArrayList<Friend> friendsInfo = new ArrayList<Friend>();
        SharedPreferences pref = getSharedPreferences(TRACE, 0);
        Set<String> traceables = pref.getStringSet(TRACE, new HashSet<String>());

        for(String traceable: traceables){
            //SmsManager.getDefault().sendTextMessage("7358080328", null, traceable, null, null);
            friendsInfo.add(new Friend(traceable.split(",")[0], traceable.split(",")[1]));
        }
        mArrayAdapter = new FriendsAdapter(getApplicationContext(), friendsInfo);
    }

    public void addToSharedPref(String key, String value){
        SharedPreferences pref = getSharedPreferences(key, 0);
        Set<String> items = pref.getStringSet(key, new HashSet<String>());
        items.add(value);
        pref.edit().putStringSet(key, items).apply();
    }
}
