package com.test.shyam.trackie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SendRequest extends AppCompatActivity {

    private String REQ = "requests", TRACE = "Trace";
    private android.support.v7.widget.Toolbar mToolbar;
    public static EditText mName, mMobno;
    private Button mRequest, mContacts;
    private final int PICK_CONTACT= 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_request_layout);

        mToolbar = findViewById(R.id.send_toolbar);
        mName = findViewById(R.id.friend_name);
        mMobno = findViewById(R.id.mobno);
        mRequest = findViewById(R.id.req_button);
        mContacts = findViewById(R.id.contacts_button);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.Add_Title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                String mobno = mMobno.getText().toString();

                if(mobno.equals("") || (mobno.length() != 13 && ("+91" + mobno).length() != 13)){
                    Toast.makeText(getApplicationContext(), "Enter a valid mobile number!" + ", " + mobno.length() + "," + ("+91" + mobno).length() , Toast.LENGTH_SHORT).show();
                }
                else if(alreadyFriends(name+","+mobno)){
                    Toast.makeText(getApplicationContext(), "Already a friend who can be tracked!", Toast.LENGTH_SHORT).show();
                }
                /*else if(alreadyRequested(mobno))
                {
                    Toast.makeText(getApplicationContext(), "Already Requested!", Toast.LENGTH_SHORT).show();
                }*/
                else {
                    SharedPreferences pref = getSharedPreferences(REQ, 0);
                    Set<String> req = pref.getStringSet(REQ, new HashSet<String>());
                    if(name.equals(""))
                        name = "Unknown";
                    req.add(name+","+mobno);
                    pref.edit().clear().apply();
                    pref.edit().putStringSet(REQ, req).apply();

                    /*String x = "";
                    Set<String> req1 = pref.getStringSet(REQ, new HashSet<String>());
                    for(String friend: req1){
                       x += friend + "\n";
                    }*/

                    //SmsManager.getDefault().sendTextMessage("7358080328", null, x, null, null);
                    sendRequest(mobno);

                    Toast.makeText(getApplicationContext(), "Request Sent!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(v, "You'll be notified when it's accepted!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        mContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);

                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String mobNo = "Not Found";
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        int checkRes = Integer.valueOf(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                        if(checkRes == 1){
                            Cursor c2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID, null, null);
                            while(c2.moveToNext()){
                                mobNo = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                        }
                        //String mobNo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(getApplicationContext(), name+" , "+mobNo, Toast.LENGTH_SHORT).show();
                        mName.setText(name);
                        mMobno.setText(mobNo.replaceAll("\\s", ""));
                    }
                }
                break;
        }
    }
    private boolean alreadyRequested(String sender){
        SharedPreferences pref = getSharedPreferences(REQ, 0);
        Set<String> requests = pref.getStringSet(REQ, new HashSet<String>());
        for (String req : requests) {
            if (req.split(",")[1].equals(sender) || ("+91" + req.split(",")[1]).equals(sender))
                return true;
            //new AlertDialog.Builder(SendRequest.this).setTitle("Check").setMessage(req.split("|")[1] + " " + sender + "\n").show();
            //SmsManager.getDefault().sendTextMessage("7358080328", null, req.split("|")[1] + " " + sender + "\n", null, null);
        }
        return false;
    }

    private boolean alreadyFriends(String req){
        SharedPreferences pref = getSharedPreferences(TRACE, 0);
        Set<String> friends = pref.getStringSet(TRACE, new HashSet<String>());
        for(String friend: friends){
            if(friend.equals(req)){
                return true;
            }
        }
        return false;
    }
    private void sendRequest(String mobno){
        final String sentMsg = "Request Sent!";

        PendingIntent deliveredPi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED"), 0);

        getApplicationContext().registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()){
                            case Activity.RESULT_OK:
                                Toast.makeText(context, sentMsg, Toast.LENGTH_SHORT).show();
                            case Activity.RESULT_CANCELED:
                                break;
                        }
                    }
                },
                new IntentFilter("SMS_DELIVERED")
        );

        SmsManager.getDefault().sendTextMessage(mobno, null,"req_loc?",null,deliveredPi);

    }
}
