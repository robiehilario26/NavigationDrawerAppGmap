package com.xperiasola.philubiq64wi.navigationdrawerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;

import java.util.ArrayList;

/**
 * Created by philUbiq64wi on 1/31/2018.
 */

public class InvitationAdapter extends ArrayAdapter<InvitationModel> {


    /**
     * TODO: Remove this whole class if no further usage in final release
     * This is the original code for populating listView item in invitation
     * activity. This code has new version class which is "InvitationAdapterWithFilter".
     * Code is maintain for reference purposed only
     * */

        public InvitationAdapter(Context context, int resource, ArrayList<InvitationModel> invites) {
        super(context, resource, invites);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //** Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.invitation_list, parent, false);
        }


        //** Get the data item for this position
        InvitationModel invites = getItem(position);

        //** Lookup view for data population
        TextView ticketId = convertView.findViewById(R.id.textView_invitation_ticketId);
        TextView pending = convertView.findViewById(R.id.textView_requestStatus);
        TextView place = convertView.findViewById(R.id.textViewActivityName);
        TextView accepted = convertView.findViewById(R.id.textView_requestStatusApproved);
        TextView rejected = convertView.findViewById(R.id.textView_requestStatusReject);
        TextView address = convertView.findViewById(R.id.textView_vicinity);
        TextView sender = convertView.findViewById(R.id.textView_sender);
        TextView senderId = convertView.findViewById(R.id.textView_invitation_senderId);

        //** Populate the data into the template view using the data object
        ticketId.setText(invites.getTicket_id());
        place.setText("Going at " + invites.getPlace() +" with " + invites.getPeople() + " people");
        pending.setText(invites.getPending() + " Pending");
        accepted.setText(invites.getAccepted() + " Accepted");
        rejected.setText(invites.getRejected() + " Rejected");
        address.setText("Address: " + invites.getAddress());
        senderId.setText(invites.getSenderId());
        sender.setText(invites.getSender());

        //** Return the completed view to render on screen


        return convertView;
    }


}
