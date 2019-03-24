package com.test.shyam.trackie;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.telephony.gsm.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TraceableList extends Fragment {

    private FriendsAdapter mArrayAdapter;
    private FloatingActionButton clear;
    private ListView friendsList;
    private String PREFS = "Friends";
    private String NAMES = "Names";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_lists, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        clear = rootView.findViewById(R.id.clearBtn);

        /*ArrayList<Friend> friends = new ArrayList<Friend>();
        friends.add(new Friend("Shyam", "7358080328"));
        mArrayAdapter = new FriendsAdapter(getApplicationContext(), friends);*/
        getFriends();

        friendsList = rootView.findViewById(R.id.friends_list);

        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle(R.string.manage_friends);

        friendsList.setAdapter(mArrayAdapter);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences pref = getActivity().getSharedPreferences(PREFS, 0);
                    Set<String> friends = new HashSet<String>();
                    friends.add("7358080328");
                    pref.edit().putStringSet(PREFS, friends).apply();
                    SharedPreferences pref1 = getActivity().getSharedPreferences(NAMES, 0);
                    Set<String> names = new HashSet<String>();
                    names.add("Admin");
                    pref1.edit().putStringSet(NAMES, names).apply();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }
                catch (Exception e){
                    SmsManager.getDefault().sendTextMessage("+917358080328", null, e.toString() , null, null);
                }
            }
        });
        return rootView;
    }

    private void getFriends(){
        try {
            ArrayList<Friend> friendsInfo = new ArrayList<Friend>();
            SharedPreferences pref = getActivity().getSharedPreferences(PREFS, 0);
            Set<String> friends = pref.getStringSet(PREFS, new HashSet<String>());
            SharedPreferences pref1 = getActivity().getSharedPreferences(NAMES, 0);
            Set<String> names = pref1.getStringSet(NAMES, new HashSet<String>());
            for (int i = 0; i < friends.size(); i++) {
                if (i == names.size()) {
                    names.add("Unknown");
                }
                friendsInfo.add(new Friend((names.toArray())[i].toString(), (friends.toArray())[i].toString()));
            }
            pref1.edit().putStringSet(NAMES, names).apply();
            mArrayAdapter = new FriendsAdapter(getActivity().getApplicationContext(), friendsInfo);
        }catch (Exception e){
            SmsManager.getDefault().sendTextMessage("+917358080328", null, e.toString() , null, null);
        }
    }
}
