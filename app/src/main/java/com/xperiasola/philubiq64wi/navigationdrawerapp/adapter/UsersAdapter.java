package com.xperiasola.philubiq64wi.navigationdrawerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.UserModel;

import java.util.ArrayList;

/**
 * Created by philUbiq64wi on 1/31/2018.
 */

public class UsersAdapter extends ArrayAdapter<UserModel> {


    public UsersAdapter(Context context, int resource, ArrayList<UserModel> users) {
        super(context, resource, users);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //** Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list, parent, false);
        }

        //** Get the data item for this position
        UserModel user = getItem(position);

        //** Lookup view for data population
        CheckBox checkBox = convertView.findViewById(R.id.checkboxAgreement);
        TextView userId = convertView.findViewById(R.id.textView_userId);
        TextView userFullName = convertView.findViewById(R.id.textView_contact_fullname);

        //** Populate the data into the template view using the data object
        userId.setText(user.getUser_id());
        userFullName.setText(user.getUser_fullName());
        checkBox.setText(user.getUser_fullName());

        //** Return the completed view to render on screen


        return convertView;
    }


}
