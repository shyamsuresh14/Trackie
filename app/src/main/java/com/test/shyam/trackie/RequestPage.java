package com.test.shyam.trackie;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;


public class RequestPage extends AppCompatActivity {

    private String TRACK = "Friends";
    private Toolbar mToolbar;
    private TextView mSender;
    private Button mAccept, mDecline;
    private String sender;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_layout);

        mToolbar = findViewById(R.id.request_toolbar);
        mSender = findViewById(R.id.sender);
        mAccept = findViewById(R.id.accept);
        mDecline = findViewById(R.id.decline);

        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Track Request");

        name = findName(sender);
        if(name.equals("Unknown"))
            mSender.setText(sender);
        else
            mSender.setText(name);

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(TRACK, 0);
                Set<String> friends = pref.getStringSet(TRACK, new HashSet<String>());
                friends.add(name + "," + sender);
                pref.edit().clear().apply();
                pref.edit().putStringSet(TRACK, friends).apply();

                SmsManager.getDefault().sendTextMessage(sender, null, "acc", null, null);

                Intent intent1 = new Intent(getApplicationContext() , FriendsList.class);
                finish();
                startActivity(intent1);
            }
        });

        mDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager.getDefault().sendTextMessage(sender, null, "rej", null, null);
                finish();
            }
        });
    }

    String findName(String sender){
        String friend = "Unknown";

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String mobNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if(mobNo.replaceAll("\\s", "").equals(sender) || ("+91" + mobNo.replaceAll("\\s", "")).equals(sender))
                            friend = name;
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        return friend;
    }
}
