package com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customAlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.AlertDialogAdapter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.CustomExpandableListAdapter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.InvitationAdapterWithFilter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.expandableListView.ExpandableListDataPump;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by philUbiq64wi on 3/6/2018.
 */

public class CustomDialogClass {
    public int ticketId;
    private String senderName, requestStatus, eventDate,
            eventStart, eventEnd, eventTitle, eventRemarks;
    private InvitationModel invitationModel = new InvitationModel();
    private ArrayList<InvitationModel> invited;
    private List<String> remarks = new ArrayList<>();
    private List<String> addressLocation = new ArrayList<>();
    private static ListView lvInvitations;
    private int userId;
    private DatabaseHelper myDb;
    private ArrayList<InvitationModel> invitations;
    public static InvitationAdapterWithFilter adapterWithFilter;
    public Activity activity;
    private boolean isActive = false;
    private SimpleDateFormat spf;
    private Date newDate;
    private Dialog dialog;
    private ImageView personList, closeList;
    private TextView textViewContent, textViewDate;
    private Button dialogButtonAccept, dialogButtonReject;
    private int top, index, day;
    private boolean isStatusPositive, isStatusNegative;


    public void showDialog(final Activity activity, String msg) {
        // Set default value
        isStatusPositive = true; // Enabled
        isStatusNegative = true; // Enabled

        // Set content activity
        this.activity = activity;

        // initialize database connection
        myDb = new DatabaseHelper(activity);

        // Create alert dialog
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true); // dialog close when click/touch outside the area
        dialog.setContentView(R.layout.custom_dialog); // set view layout
        // Set background to transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set object id
        textViewContent = dialog.findViewById(R.id.text_dialog_sent);
        personList = dialog.findViewById(R.id.icon_list_of_persons);
        closeList = dialog.findViewById(R.id.icon_close_list);
        dialogButtonAccept = dialog.findViewById(R.id.btnAccept);
        dialogButtonReject = dialog.findViewById(R.id.btnReject);
        textViewDate = dialog.findViewById(R.id.text_dialog_event_info_sent);

        // Call function
        populateExpandedListViewInfo();
        setInvitationContent();
        setDialogListeners();


        // Setter Model value
        invitationModel.setFk_user_id(DataHolder.getData().toString());
        invitationModel.setTicket_id(String.valueOf(ticketId));

        // If event is not yet expired
        if (this.day >= 0) {
            // Call function
            buttonState();
        } else {            // If event is already expired all button will not be clickable
            Toast.makeText(activity, "Event already expired", Toast.LENGTH_SHORT).show();
            isStatusPositive = false; // Disabled
            isStatusNegative = false; // Disabled
        }

        // Set button if disabled/enabled
        dialogButtonAccept.setEnabled(isStatusPositive);
        dialogButtonReject.setEnabled(isStatusNegative);
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
                              String eventAddress, String day) {
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
        this.day = Integer.parseInt(day);
    }

    // Fetch all Invitation List with filter list
    public void fetchInvitationListWithFilter(Activity activity) {

        isActive = true;
        lvInvitations = this.activity.findViewById(R.id.lvInvitations_fragment_home);


        userId = PreferenceData.getUserLoggedInId(activity);
        // Pass userId value to DataHolder
        DataHolder.setData(String.valueOf(userId));

        invitations = myDb.fetchInvitationsRecieve(DataHolder.getData().toString());
        adapterWithFilter = new InvitationAdapterWithFilter(activity, invitations);
        lvInvitations.setAdapter(adapterWithFilter);
        // Repositioning the scroll view on current state
        lvInvitations.setSelectionFromTop(this.index, this.top);


    }


    // Check if this class has been use or not
    public boolean isClassActive() {
        if (isActive) {
            return true;
        }
        return false;
    }

    public void populateExpandedListViewInfo() {
        // TODO: For testing. Expandable list view
        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;
        final List<String> expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;


        // TODO: For testing. Expandable list view
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
        // Accept button click listener
        dialogButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationModel.setStatus("Accepted");
                //** Call update method for Accepting invitation
                boolean isUpdate = myDb.updateInvitationData(invitationModel);
                if (isUpdate) {
                    // fetch invitation list
                    Toast.makeText(activity, "Accepted", Toast.LENGTH_SHORT).show();
                    fetchInvitationListWithFilter(activity);
                } else {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        // Reject button click listener
        dialogButtonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitationModel.setStatus("Rejected");
                //** Call update method for Accepting invitation
                boolean isUpdate = myDb.updateInvitationData(invitationModel);
                if (isUpdate) {
                    //** Fetch invitation list
                    fetchInvitationListWithFilter(activity);
                    //** Update Successfully
                    Toast.makeText(activity, "Rejected", Toast.LENGTH_SHORT).show();
                } else {
                    //** Update Failed
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                }
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
                dialog.setContentView(R.layout.alertdialog_listview_layout); // Layout file name
                ImageView iconClose = dialog.findViewById(R.id.icon_close);
                ListView listView = dialog.findViewById(R.id.list); // ListView id
                //** Create the adapter to convert the array to views
                AlertDialogAdapter adapter = new AlertDialogAdapter(activity, 0, invited);
                adapter.isOnSentList(false);
                //** Attach the adapter to a ListView
                listView.setAdapter(adapter);

                // Close dialog when close icon click
                iconClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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

        // TODO: Remove this on final release when no further usage
        // Check if has Remarks
        if (this.eventRemarks.equalsIgnoreCase("None")) {
            // Set to blank if no remarks
            eventRemarks = "";
        } else {
            // Set remarks
            eventRemarks = "<br/><br/><b>Remarks: </b>" + this.eventRemarks;
        }

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

    // Get current height and width of list view scroll position
    public void itemPosition(int index, int top) {
        this.index = index;
        this.top = top;
    }

    // Set button accept/reject enable state depend on the request status
    public void buttonState() {
        // If request is once accepted the button accept will be disabled
        if (this.requestStatus.equals("Accepted")) {
            isStatusPositive = false; // Disabled
        }
        // If request is once rejected the button accept will be disabled
        if (this.requestStatus.equals("Rejected")) {
            isStatusNegative = false; // Disabled
        }
    }


}
