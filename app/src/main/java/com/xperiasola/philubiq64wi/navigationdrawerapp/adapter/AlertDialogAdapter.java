package com.xperiasola.philubiq64wi.navigationdrawerapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philUbiq64wi on 2/6/2018.
 */

public class AlertDialogAdapter extends ArrayAdapter<InvitationModel> {

    /**
     * This code is for populating data inside alert dialog box with listView
     * item. Display each user that has been invited and display request
     * status such as Pending,Accepted and Reject
     ***/

    private boolean state;


    public AlertDialogAdapter(Context context, int resource, ArrayList<InvitationModel> invites) {
        super(context, resource, invites);

    }


    public boolean isOnSentList(boolean state) {
        if (state) { // if state is true it means user is in Sent list
            return this.state = true;
        }
        return this.state = false; // if state is false it means user is in Invitation list
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //** Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item_layout, parent, false);
        }

        //** Get the data item for this position
        InvitationModel invites = getItem(position);
        //** Lookup view for data population

        final TextView requestStatus = convertView.findViewById(R.id.ad_textView_requestStatus);
        TextView user = convertView.findViewById(R.id.singleItem);
        ImageView closeImage = convertView.findViewById(R.id.image_close);
        final TextView id = convertView.findViewById(R.id.ad_textView_invitation_ticketId);

        // Check state if user is in Sent list or Invitation list
        if (this.isOnSentList(state)) {
            // If user is in Sent List show the close button
            closeImage.setVisibility(View.VISIBLE);
        } else {  // If user is in Invitation List hide the close button
            closeImage.setVisibility(View.GONE);
        }


        //** Populate the data into the template view using the data object
        id.setText(invites.getId());
        user.setText(invites.getPeople());
        requestStatus.setText(invites.getStatus());


        //** Changing TextColor according to status
        if (invites.getStatus().equalsIgnoreCase("Accepted")) {
            //** Set color to green
            requestStatus.setTextColor(Color.parseColor("#3d9128"));
        }
        if (invites.getStatus().toLowerCase().equalsIgnoreCase("Rejected")) {
            //** Set color to red
            requestStatus.setTextColor(Color.parseColor("#e62f1f"));
        }
        if (invites.getStatus().toLowerCase().equalsIgnoreCase("Pending")) {
            //** Set color to orange
            requestStatus.setTextColor(Color.parseColor("#e76d27"));
        }

        // Return the completed view to render on screen

        return convertView;
    }




}
