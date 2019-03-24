package com.test.shyam.trackie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FriendsList extends AppCompatActivity {

    private Toolbar mToolbar;
    private FriendsAdapter mArrayAdapter;
    private FloatingActionButton clear;
    private ListView friendsList;
    private String TRACK = "Friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        mToolbar = findViewById(R.id.friends_toolbar);
        clear = findViewById(R.id.clearBtn);
        getFriends();

        friendsList = findViewById(R.id.friends_list);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.trackers);

        friendsList.setAdapter(mArrayAdapter);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(TRACK, 0);
                Set<String> friends = new HashSet<String>(); friends.add("7358080328");
                pref.edit().putStringSet(TRACK, friends).apply();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void getFriends(){
        ArrayList<Friend> friendsInfo = new ArrayList<Friend>();
        SharedPreferences pref = getSharedPreferences(TRACK, 0);
        Set<String> friends = pref.getStringSet(TRACK, new HashSet<String>());

       for(String friend: friends){
            friendsInfo.add(new Friend(friend.split(",")[0], friend.split(",")[1]));
       }

       mArrayAdapter = new FriendsAdapter(getApplicationContext(), friendsInfo);
    }

}
