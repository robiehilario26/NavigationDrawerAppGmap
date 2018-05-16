package com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customAlertDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.activities.Contacts;
import com.xperiasola.philubiq64wi.navigationdrawerapp.activities.EditInvitation;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.AlertDialogAdapter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.CustomExpandableListAdapter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.InvitationAdapterWithFilter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.EventInfoModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.expandableListView.ExpandableListDataPump;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by philUbiq64wi on 3/15/2018.
 */

public class CustomDialogSentInfoClass extends EventInfoModel {
    public int ticketId, scrollPosition;
    private String senderName, requestStatus, eventDate,
            eventStart, eventEnd, eventTitle,
            eventRemarks, placeName, getPersonId, personId, personRequestStatus;
    private InvitationModel invitationModel = new InvitationModel();
    private ArrayList<InvitationModel> invited;
    private List<String> remarks = new ArrayList<>();
    private List<String> addressLocation = new ArrayList<>();
    private static ListView lvInvitations;
    private int userId;
    private DatabaseHelper myDb;
    private ArrayList<InvitationModel> invitations;
    private InvitationAdapterWithFilter adapterWithFilter;
    public Activity activity;
    private boolean isAdvance;
    public static boolean IS_ACTIVE = false;
    private SimpleDateFormat spf;
    private Date newDate;
    private Dialog dialog;
    private ImageView personList, closeList;
    private TextView textViewContent, textViewDate;
    private Button dialogButtonEditEvent, dialogButtonCancelEvent;
    private int top, index;
    private int day, getItemPosition;
    private boolean isStatusEditable, isStatusCancellable;
    private AlertDialogAdapter adapterInvitedPerson;


    public void showDialog(final Activity activity, Context context) {
        // Set default value
        isStatusEditable = false; // Enabled
        isStatusCancellable = false; // Enabled

        // Set content activity
        this.activity = activity;

        // initialize database connection
        myDb = new DatabaseHelper(activity);

        // Create alert dialog
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true); // dialog close when click/touch outside the area
        dialog.setContentView(R.layout.custom_dialog_sent_info); // set view layout
        // Set background to transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set object id
        textViewContent = dialog.findViewById(R.id.text_dialog_sent);
        personList = dialog.findViewById(R.id.icon_list_of_persons);
        closeList = dialog.findViewById(R.id.icon_close_list);
        dialogButtonEditEvent = dialog.findViewById(R.id.btnEditEvent);
        dialogButtonCancelEvent = dialog.findViewById(R.id.btnCancelEvent);
        textViewDate = dialog.findViewById(R.id.text_dialog_event_info_sent);

        // Call function
        populateExpandedListViewInfo();
        setInvitationContent();
        setDialogListeners();


        // Setter Model value
        invitationModel.setFk_user_id(DataHolder.getData().toString());
        invitationModel.setTicket_id(String.valueOf(ticketId));


        if (this.day >= 2) {
            // If current date is more than or equal to 2 then enable button
            isStatusEditable = true; // enable edit event
            isStatusCancellable = true; // enable cancel event
        } else if (this.day >= 0) {
            isStatusEditable = true; // enable edit event
            isStatusCancellable = true; // enable cancel event
        } else {
            // Event cant be edited or cancelled
            Toast.makeText(activity, "Event already expired", Toast.LENGTH_SHORT).show();
        }


        // Set button if disabled/enabled
        dialogButtonEditEvent.setEnabled(isStatusEditable);
        dialogButtonCancelEvent.setEnabled(isStatusCancellable);
        dialog.show();


    }

    // Format month of date into words
    public SimpleDateFormat dateFormat(String date) {
        spf = new SimpleDateFormat("MM/dd/yyyy");
        newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return spf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
    }

    // Fetch clicked item data from Home Fragment ListView
    public void getTicketInfo(int id, String senderName, String requestStatus
            , String eventDate, String eventStart, String eventEnd,
                              String eventTitle, String eventRemarks,
                              String eventAddress, Boolean isAdvance,
                              String placeName, int scrollPosition,
                              InvitationAdapterWithFilter adapterWithFilter) {
        this.ticketId = id;
        this.senderName = senderName;
        this.requestStatus = requestStatus;
        this.eventDate = eventDate;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventTitle = eventTitle;
        // Clear Address list before adding
        addressLocation.clear();
        remarks.clear();
        // Check remarks
        if (eventRemarks.isEmpty()) { // If empty set to None
            this.eventRemarks = "None";
        }
        this.eventRemarks = eventRemarks;
        this.addressLocation.add(eventAddress);// Add address into list
        this.remarks.add(eventRemarks);// Add remarks into list
        this.isAdvance = isAdvance;
        this.placeName = placeName;
        this.scrollPosition = scrollPosition;
        this.adapterWithFilter = adapterWithFilter;
    }


    // TODO: Remove this code when no further usage in final release
    // Fetch all Invitation List with filter list
    public void fetchInvitationListWithFilter(Activity activity) {


        lvInvitations = this.activity.findViewById(R.id.lvInvitations_fragment_sent);

        userId = PreferenceData.getUserLoggedInId(activity);
        // Pass userId value to DataHolder
        DataHolder.setData(String.valueOf(userId));

        invitations = myDb.fetchInvitationsSent(DataHolder.getData().toString());
        adapterWithFilter = new InvitationAdapterWithFilter(activity, invitations);
        lvInvitations.setAdapter(adapterWithFilter);
        // Repositioning the scroll view on current state
        lvInvitations.setSelectionFromTop(this.index, this.top);


    }

    // Set edit state
    public void checkEditState(final Boolean state) {
        this.IS_ACTIVE = state;
    }


    // Check if user do changes/edit in invitation or not
    public final boolean isThereChanges() {
        if (IS_ACTIVE) {
            return true;
        } else {
            return false;
        }

    }

    public void populateExpandedListViewInfo() {
        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;
        final List<String> expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;

        expandableListView = dialog.findViewById(R.id.expandableListView);

        // Fetch invited person then put into List<String>

        ExpandableListDataPump expandableListDataPump = new ExpandableListDataPump();
        expandableListDataPump.fethRemarks(remarks); // Set remarks
        expandableListDataPump.fetchAddress(addressLocation); // Set address location

        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(activity,
                expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        // Set listener when expandedListView expand
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // TODO: for future reference
                /*Toast.makeText(activity,
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        // Set listener when expandedListView collapse
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                // TODO: for future reference
                /*Toast.makeText(activity,
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();*/

            }
        });

        // Set listener when clicking on child item inside expanded ListView
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO: for future reference
               /* Toast.makeText(
                        activity,
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();*/
                return false;
            }
        });
    }


    // Set all button/image click listener inside dialog
    public void setDialogListeners() {


        final String finalTitle;
        final String finalPlaceName;
        final String finalDate;
        final String finalStart;
        final String finalEnd;
        final String finalRemarks;
        final String finalPlace;
        final Boolean isAdvance;

        finalTitle = this.eventTitle;
        finalPlaceName = this.placeName;
        finalDate = this.eventDate;
        finalStart = this.eventStart;
        finalEnd = this.eventEnd;
        finalRemarks = this.eventRemarks;
        finalPlace = this.addressLocation.get(0);
        isAdvance = this.isAdvance;


        // Accept button click listener
        dialogButtonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // EventInfoModel Parcelable class
                EventInfoModel dataToSend = new EventInfoModel(finalTitle, finalDate, finalStart,
                        finalEnd, finalRemarks, finalPlace, isAdvance, finalPlaceName, ticketId);

                Intent intent = new Intent(activity, EditInvitation.class);
                intent.putExtra("myDataKey", dataToSend); // Pass value
                IS_ACTIVE = false;
                activity.startActivity(intent); // Open activity
                dialog.dismiss();
            }
        });

        // Reject button click listener
        dialogButtonCancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.updateTicketStatus(String.valueOf(ticketId));
                adapterWithFilter.deleteItem(scrollPosition);
                //** Update changes in ListView
                adapterWithFilter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        // On clicking person icon display the invited person name inside dialog
        personList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //** Fetch all user to ArrayList by ticket_id
                invited = myDb.fetchInvitationsDetailsObj(String.valueOf(ticketId));

                final Dialog dialog = new Dialog(activity);
                // This must be called before adding content
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Set to no title
                // Set background to transparent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Add content view
                dialog.setContentView(R.layout.alertdialog_listview_layout_sent); // Layout file name
                final ListView listView = dialog.findViewById(R.id.list); // ListView id
                ImageView iconClose = dialog.findViewById(R.id.icon_close); // ListView id
                Button buttonAddUser = dialog.findViewById(R.id.btn_addUser);


                /**
                 *  Set add user button enabled/disabled. if it is not editable it will
                 *  automatically disabled.
                 *  If event is still editable the user can add/tag new user to join the event
                 **/
                buttonAddUser.setEnabled(isStatusEditable);

                // Create the adapter to convert the array to views
                adapterInvitedPerson = new AlertDialogAdapter(activity, 0, invited);

                /**
                 *  If invitation is still editable it can remove user from list
                 *  else if invitation is not editable or already expired the
                 *  user cant remove any user or add user
                 **/
                adapterInvitedPerson.isOnSentList(isStatusEditable);
                // Attach the adapter to a ListView
                listView.setAdapter(adapterInvitedPerson);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Initialize object id
                        TextView textViewTicketId = view.findViewById(R.id.ad_textView_invitation_ticketId);
                        TextView textViewPersonName = view.findViewById(R.id.singleItem);
                        TextView textViewStatus = view.findViewById(R.id.ad_textView_requestStatus);

                        // Pass value of id into string
                        personId = textViewTicketId.getText().toString();
                        personRequestStatus = textViewStatus.getText().toString();
                        /**
                         * TODO: This part can add validation like:
                         * 1. if user has already accept/reject
                         * the user cannot be remove vice versa depends on the modification
                         * that user want.
                         *
                         * 2. User with pending status are the only available to remove
                         *
                         * On this part the user can remove a user by clicking in list view
                         * */


                        // Check if event is still editable
                        if (isStatusEditable) { // if still editable
                            // Check request status of person into the current invitation.
                            // If request status is Rejected/Accepted the user cant be remove as for now
                            if (personRequestStatus.equalsIgnoreCase("rejected") ||
                                    personRequestStatus.equalsIgnoreCase("accepted")) {
                                // Do nothing
                            } else {
//                                 If user request status is pending
//                                 Call function for showing alert dialog
                                showMessage("Confirmation", "Remove " +
                                                textViewPersonName.getText().toString() + " ?"
                                        , personId, position);
                            }


                        }


                    }
                });


                iconClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



                // TODO: continue here 3/22/2018
                buttonAddUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ticketId
                        Toast.makeText(activity, "list " + invited.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, Contacts.class);
                        activity.startActivity(intent);

                    }
                });

                dialog.show();
            }
        });


        // Close dialog when close icon touch/click
        closeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    // Set invitation details
    public void setInvitationContent() {
        // Create string builder for appending values
        StringBuilder stringBuilder = new StringBuilder();
        // Create string variable for passing value
        String senderName, fixContent, timeContent, eventTime, timeStart,
                timeEnd, eventTitle, newDateString, eventRemarks;


        // Format month eventDate value into words
        // Call function dateFormat
        dateFormat(this.eventDate);

        // Setting value of to string
        // Setting text style and font color
        newDateString = "<b>" + spf.format(this.newDate) + "</b>";
        eventTitle = "<b><font color='#4197ec'>" + this.eventTitle + "</font></b>";
        timeStart = this.eventStart;
        timeEnd = this.eventEnd;
        fixContent = "has invited you to join this activity.";
        senderName = "<b><i><font color='#4197ec'>" + this.senderName + "</font></i></b>";
        timeContent = "Event will start on";
        eventTime = "from <b>" + timeStart + "</b>"
                + " to " + "<b>" + timeEnd + "</b>";


        // Append all string into 1 stringBuilder
        stringBuilder.append(senderName).append(" "); // inviter
        stringBuilder.append(fixContent).append("\n"); // has invited you to join this activity
        stringBuilder.append(timeContent).append(" "); // Make sure be there on 1/1/2018
        stringBuilder.append(newDateString).append(" ");
        stringBuilder.append(eventTime); // from 1:11 PM to 5:30 PM
        //stringBuilder.append(eventRemarks); // Remarks:

        // Set date value
        textViewDate.setText(Html.fromHtml(eventTitle));

        // Set textView value using StringBuilder converted to string
        textViewContent.setText(Html.fromHtml(stringBuilder.toString()));
    }

    public void itemPosition(int index, int top) {
        this.index = index;
        this.top = top;
    }

    public void getDiffDays(String day) {
        this.day = Integer.parseInt(day);
    }


    // Show alert dialog for confirmation of removing selected user
    public void showMessage(String title, String message,
                            final String personId, final int itemPosition) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                boolean isRemove = myDb.removePerson(personId);
                if (isRemove) {
                    // Remove selected person into arrayList
                    invited.remove(itemPosition);
                    // Notify changes in adapter
                    adapterInvitedPerson.notifyDataSetChanged();
                    adapterWithFilter.notifyDataSetChanged();
                    fetchInvitationListWithFilter(activity);
                    // Set to true for refreshing listView content in Sent navigation


                } else {
                    Toast.makeText(activity, "Failed to remove user " + personId, Toast.LENGTH_SHORT).show();
                }

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }


}
