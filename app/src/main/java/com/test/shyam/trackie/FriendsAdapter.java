package com.test.shyam.trackie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.YELLOW;

public class FriendsAdapter extends ArrayAdapter<Friend>{

    public FriendsAdapter(@NonNull Context context, ArrayList<Friend> friends) {
        super(context, 0, friends);
    }
    public static class ViewHolder{
        EditText name;
        TextView mobNo;
        LinearLayout.LayoutParams params;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Friend friend = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
            viewHolder.name = (EditText) convertView.findViewById(R.id.Name);
            viewHolder.mobNo = (TextView) convertView.findViewById(R.id.MobNo);
            viewHolder.params = (LinearLayout.LayoutParams) viewHolder.mobNo.getLayoutParams();
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(friend.name);
        viewHolder.mobNo.setText(friend.mobNo);
        //viewHolder.params.gravity = Gravity.CENTER;
        //viewHolder.name.setTextColor(GREEN);
        //viewHolder.mobNo.setTextColor(YELLOW);
        return convertView;
    }
}