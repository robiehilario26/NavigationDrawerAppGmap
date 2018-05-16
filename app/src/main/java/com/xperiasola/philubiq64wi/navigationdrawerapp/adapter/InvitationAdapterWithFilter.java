package com.xperiasola.philubiq64wi.navigationdrawerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;

import java.util.ArrayList;

/**
 * Created by philUbiq64wi on 2/22/2018.
 * This class populate the listView item with filter search.
 **/

public class InvitationAdapterWithFilter extends BaseAdapter implements Filterable {
    private ArrayList<InvitationModel> mOriginalValues; // Original Values
    private ArrayList<InvitationModel> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;

    public InvitationAdapterWithFilter(Context context, ArrayList<InvitationModel> mProductArrayList) {
        super();
        this.mOriginalValues = mProductArrayList;
        this.mDisplayedValues = mProductArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayedValues.get(position).getId();
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    /**
     * Delete item by position
     **/
    public void deleteItem(int position) {
        mDisplayedValues.remove(position);
        // remove(int) does not exist for arrays, you would have to write that method yourself
        // or use a List instead of an array
        notifyDataSetChanged();
    }


    private class ViewHolder {
        TextView ticketId, place, pending, accepted, rejected, address, senderId, sender, requestStatus;
        TextView eventTitle, eventDate, eventStart, eventEnd, eventRemarks, eventIsAdvance,eventPlace;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            // Set listView layout
            convertView = inflater.inflate(R.layout.invitation_list, null);

            // Initialize object
            holder.ticketId = convertView.findViewById(R.id.textView_invitation_ticketId);
            holder.pending = convertView.findViewById(R.id.textView_requestStatus);
            holder.place = convertView.findViewById(R.id.textViewActivityName);
            holder.accepted = convertView.findViewById(R.id.textView_requestStatusApproved);
            holder.rejected = convertView.findViewById(R.id.textView_requestStatusReject);
            holder.address = convertView.findViewById(R.id.textView_vicinity);
            holder.sender = convertView.findViewById(R.id.textView_sender);
            holder.senderId = convertView.findViewById(R.id.textView_invitation_senderId);
            holder.requestStatus = convertView.findViewById(R.id.textView_invitation_status);


            holder.eventTitle = convertView.findViewById(R.id.eventTitle);
            holder.eventDate = convertView.findViewById(R.id.eventDate);
            holder.eventStart = convertView.findViewById(R.id.eventStart);
            holder.eventEnd = convertView.findViewById(R.id.eventEnd);
            holder.eventRemarks = convertView.findViewById(R.id.eventRemarks);
            holder.eventIsAdvance = convertView.findViewById(R.id.eventIsAdvance);
            holder.eventPlace = convertView.findViewById(R.id.eventPlace);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ticketId.setText(mDisplayedValues.get(position).getTicket_id());
        holder.place.setText(mDisplayedValues.get(position).getEventTitle() + " at "
                + mDisplayedValues.get(position).getPlace() + " with " +
                mDisplayedValues.get(position).getPeople() + " people");
        holder.pending.setText(mDisplayedValues.get(position).getPending() + " Pending");
        holder.accepted.setText(mDisplayedValues.get(position).getAccepted() + " Accepted");
        holder.rejected.setText(mDisplayedValues.get(position).getRejected() + " Rejected");
        holder.address.setText("Address: " + mDisplayedValues.get(position).getAddress());
        holder.senderId.setText(mDisplayedValues.get(position).getSenderId());
        holder.sender.setText(mDisplayedValues.get(position).getSender());
        holder.requestStatus.setText(mDisplayedValues.get(position).getStatus());

        holder.eventTitle.setText(mDisplayedValues.get(position).getEventTitle());
        holder.eventDate.setText(mDisplayedValues.get(position).getEventDate());
        holder.eventStart.setText(mDisplayedValues.get(position).getEventStart());
        holder.eventEnd.setText(mDisplayedValues.get(position).getEventEnd());
        holder.eventRemarks.setText(mDisplayedValues.get(position).getEventRemarks());
        holder.eventIsAdvance.setText(mDisplayedValues.get(position).getEventAdvance().toString());
        holder.eventPlace.setText(mDisplayedValues.get(position).getPlace());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mDisplayedValues = (ArrayList<InvitationModel>) results.values; // has the filtered values
                System.out.println("Filter: " + mDisplayedValues.toString());
                notifyDataSetChanged();  // notifies the data with new filtered values
            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Holds the results of a filtering operation in values
                FilterResults results = new FilterResults();
                ArrayList<InvitationModel> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    // saves the original data in mOriginalValues
                    mOriginalValues = new ArrayList<>(mDisplayedValues);
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the
                 *  mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getPlace();
                        String activity = mOriginalValues.get(i).getEventTitle();
                        // Search data here
                        if (data.toLowerCase().contains(constraint.toString())
                                || activity.toLowerCase().contains(constraint.toString())) {
                            // Set search filter to all data whatever input matches to list
                            FilteredArrList.add(new InvitationModel(
                                    mOriginalValues.get(i).getId(), mOriginalValues.get(i).getFk_user_id(),
                                    mOriginalValues.get(i).getTicket_id(), mOriginalValues.get(i).getStatus(),
                                    mOriginalValues.get(i).getPlace(), mOriginalValues.get(i).getPeople(),
                                    mOriginalValues.get(i).getPending(), mOriginalValues.get(i).getAccepted(),
                                    mOriginalValues.get(i).getRejected(), mOriginalValues.get(i).getSender(),
                                    mOriginalValues.get(i).getSenderId(), mOriginalValues.get(i).getAddress(),
                                    mOriginalValues.get(i).getEventTitle(), mOriginalValues.get(i).getEventDate(),
                                    mOriginalValues.get(i).getEventStart(), mOriginalValues.get(i).getEventEnd(),
                                    mOriginalValues.get(i).getEventRemarks(),mOriginalValues.get(i).getEventAdvance(),
                                    mOriginalValues.get(i).getPlaceName()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}

