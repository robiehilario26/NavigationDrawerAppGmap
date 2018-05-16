package com.xperiasola.philubiq64wi.navigationdrawerapp.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.InvitationAdapterWithFilter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.GetCountOfDays;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customAlertDialog.CustomDialogSentInfoClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentInvitationFragment extends Fragment {
    private static ListView lvInvitations_fragment_sent;
    DatabaseHelper myDb;
    private ArrayList<InvitationModel> invitations;
    private String senderId, senderName, requestStatus, eventDate, eventStart,
            eventEnd, eventTitle, eventRemarks, eventAddress,
            ticketId, userName, userEmail, diffOfDays, today, isAdvance, placeName;
    private ListView listView;

    private InvitationAdapterWithFilter adapterWithFilter;
    private int scrollPosition;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private int userId;
    private View view;
    private CustomDialogSentInfoClass alertDialogInfoEvent;


    public SentInvitationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sent_invite, container, false);
        getActivity().invalidateOptionsMenu();

        //** Initialize database connection
        myDb = new DatabaseHelper(getActivity());

        //** Initialize object
        lvInvitations_fragment_sent = view.findViewById(R.id.lvInvitations_fragment_sent);

        // Initialize custom dialog class
        alertDialogInfoEvent = new CustomDialogSentInfoClass();

        //** Call function
        fetchInvitationListWithFilter();

        //TODO: The function below is the first version of listview without filter
        // fetchInvitationList();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_search: //** Contact sub menu action bar
                Toast.makeText(getContext(), "action_search!", Toast.LENGTH_LONG).show();
                handleMenuSearch();
                break;

            case R.id.action_clear_notifications: //** Clear notification sub menu action bar
                Toast.makeText(getContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        super.onPrepareOptionsMenu(menu);
    }

    // Show search box area
    protected void handleMenuSearch() {

        ActionBar action = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // ActionBar action = getSupportActionBar(); // get the actionbar

        if (isSearchOpened) { // test if the search is open
            // settingMenu.setVisible(true); // show setting menu icon
            //bInvitation.setVisibility(View.VISIBLE);
            action.setDisplayShowCustomEnabled(false); // disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); // show the title in the action bar

            adapterWithFilter.getFilter().filter(""); // fetch all data in list view item

            // add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_24dp));

            // hide the keyboard
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


            isSearchOpened = false;

        } else { // open the search entry
            // bInvitation.setVisibility(View.GONE);
            //settingMenu.setVisible(false); // hide setting menu icon
            action.setDisplayShowCustomEnabled(true); // enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);// add the custom view
            action.setDisplayShowTitleEnabled(false); // hide the title

            edtSeach = action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapterWithFilter.getFilter().filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            // open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            // add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_white_24dp));

            isSearchOpened = true;
        }
    }


    // Fetch all Invitation List with filter list
    public void fetchInvitationListWithFilter() {
        // Fetch current user widgetActivity()
        userEmail = PreferenceData.getLoggedInEmailUser(getActivity());
        userName = PreferenceData.getUserLoggedInName(getActivity());
        userId = PreferenceData.getUserLoggedInId(getActivity());
        // Pass value to DataHolder
        DataHolder.setData(String.valueOf(userId));
        //TODO: This function is for storing login state and storing userId
        // For set user loggedin status
        PreferenceData.setUserLoggedInStatus(getActivity(), true, userId, userEmail, userName);
        //PreferenceData.getUserLoggedInStatus(getActivity());        // Get User Logged In status . true = login

        invitations = myDb.fetchInvitationsSent(DataHolder.getData().toString());
        adapterWithFilter = new InvitationAdapterWithFilter(getActivity(), invitations);

        listView = view.findViewById(R.id.lvInvitations_fragment_sent);
        lvInvitations_fragment_sent.setAdapter(adapterWithFilter);


        lvInvitations_fragment_sent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //** Find TextView value using id
                scrollPosition = position;
                TextView textView_ticketId = view.findViewById(R.id.textView_invitation_ticketId);
                TextView textView_senderId = view.findViewById(R.id.textView_invitation_senderId);
                TextView textView_senderName = view.findViewById(R.id.textView_sender);
                TextView textView_eventAddress = view.findViewById(R.id.textView_vicinity);
                TextView textView_status = view.findViewById(R.id.textView_invitation_status);
                TextView textView_eventTitle = view.findViewById(R.id.eventTitle);
                TextView textView_eventDate = view.findViewById(R.id.eventDate);
                TextView textView_evenStart = view.findViewById(R.id.eventStart);
                TextView textView_eventEnd = view.findViewById(R.id.eventEnd);
                TextView textView_eventRemarks = view.findViewById(R.id.eventRemarks);
                TextView textView_eventisAdvance = view.findViewById(R.id.eventIsAdvance);
                TextView textView_placeName = view.findViewById(R.id.eventPlace);

                // Get textView value then pass into string variable
                isAdvance = textView_eventisAdvance.getText().toString(); // is two days advance true/false
                placeName = textView_placeName.getText().toString(); // place name
                senderName = textView_senderName.getText().toString(); // Sender Name
                senderId = textView_senderId.getText().toString(); // Sender id
                ticketId = textView_ticketId.getText().toString(); // Event ticket id
                eventDate = textView_eventDate.getText().toString(); // Date of event
                requestStatus = textView_status.getText().toString(); // Request status
                eventAddress = textView_eventAddress.getText().toString(); // Address
                eventTitle = textView_eventTitle.getText().toString(); // Event title
                eventStart = textView_evenStart.getText().toString(); // Start time of event
                eventEnd = textView_eventEnd.getText().toString(); // End time of event
                eventRemarks = textView_eventRemarks.getText().toString();


                // Date today
                today = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

                // Call separate class to calculate the difference from Current
                // date today to the date of the event
                GetCountOfDays getCountOfDays = new GetCountOfDays();
                diffOfDays = getCountOfDays.getCountOfDays(today, eventDate);


                // Pass the result
                alertDialogInfoEvent.getDiffDays(diffOfDays);

                /**
                 * TODO: For testing as of 3/16/2018. This code show custom dialog box
                 * This code is a separate class for accepting/reject request
                 * */
                // Call custom dialog box
                // CustomDialogClass alert = new CustomDialogClass();
                alertDialogInfoEvent.getTicketInfo(Integer.parseInt(ticketId), senderName,
                        requestStatus, eventDate, eventStart, eventEnd,
                        eventTitle, eventRemarks, eventAddress, Boolean.valueOf(isAdvance),
                        placeName, scrollPosition, adapterWithFilter);


                // save index and top position
                int index = lvInvitations_fragment_sent.getFirstVisiblePosition();
                View v = lvInvitations_fragment_sent.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - lvInvitations_fragment_sent.getPaddingTop());
                // restore index and position
                alertDialogInfoEvent.itemPosition(index, top);

                // Show alert dialog
                alertDialogInfoEvent.showDialog(getActivity(), getContext());


            }


        });

    }


    //    TODO:  for reference
    public boolean showSnackBar(final String id) {

        //** Remove item in ListView by position
        adapterWithFilter.deleteItem(scrollPosition);
        //** Update changes in ListView
        adapterWithFilter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Invitation cancelled",
                Snackbar.LENGTH_LONG).setDuration(3000);
        snackbar.setActionTextColor(ContextCompat.getColor(getContext(),
                R.color.colorPrimaryDark)).show();

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                // See Snackbar.Callback docs for event details
                if (event != DISMISS_EVENT_ACTION) {
                    // will be true if user not click on Action button (for example: manual dismiss, dismiss by swipe
                    // Update table
                    myDb.updateTicketStatus(id);
                    adapterWithFilter.notifyDataSetChanged();
                    //  Toast.makeText(Invitation.this, "Action: Dismiss " + ticketId, Toast.LENGTH_LONG).show();
                } else {
                    // When user click undo
                    Toast.makeText(getActivity(), "Action: Undo", Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onShown(Snackbar snackbar) {
                snackbar.setAction(R.string.cancel_invite, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        invitations = myDb.fetchInvitationsSent(DataHolder.getData().toString());
                        adapterWithFilter = new InvitationAdapterWithFilter(getActivity(), invitations);
                        //** Attach the adapter to a ListView
                        listView.setAdapter(adapterWithFilter);
                        adapterWithFilter.notifyDataSetChanged();
                        // Alternative solution for maintaining the current position when cancelling invitation
                        listView.setSelection(scrollPosition);

                    }
                });
            }
        });
        return true;
    }


    // Check state on resume state
    @Override
    public void onResume() {
        // Check if there is changes happen when editing event information
        Boolean checkState = alertDialogInfoEvent.isThereChanges();
        // Check state
        if (checkState) {
            // If true refresh listView
            fetchInvitationListWithFilter();
            // set to false to prevent repeated refresh
            alertDialogInfoEvent.checkEditState(false);
        }
        super.onResume();
    }


}
