package com.xperiasola.philubiq64wi.navigationdrawerapp.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.AlertDialogAdapter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.InvitationAdapterWithFilter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customAlertDialog.CustomDialogClass;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.GetCountOfDays;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static ListView lvInvitations;
    DatabaseHelper myDb;
    private ArrayList<InvitationModel> invitations;
    private ArrayList<InvitationModel> invited;
    private String ticketId, userName, userEmail;
    private String senderId, senderName, requestStatus, eventDate, eventStart,
            eventEnd, eventTitle, eventRemarks, eventAddress, diffOfDays;
    private ListView listView;


    private InvitationAdapterWithFilter adapterWithFilter;
    private int scrollPosition;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private int userId;
    private View view;
    private CustomDialogClass alertDialogInfoEvent;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().invalidateOptionsMenu();

        //** Initialize database connection
        myDb = new DatabaseHelper(getActivity());

        //** Initialize object
        lvInvitations = view.findViewById(R.id.lvInvitations_fragment_home);

        //** Call function
        fetchInvitationListWithFilter();


        alertDialogInfoEvent = new CustomDialogClass();

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

            case R.id.action_mark_all_read: //** Mark all read sub menu action bar
                Toast.makeText(getContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
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
        // Check if CustomDialogClass has been user or not
        if (alertDialogInfoEvent.isClassActive()) {
            // If class is been used adapter of this Fragment class will be updated
            this.adapterWithFilter = alertDialogInfoEvent.adapterWithFilter;
        }

        ActionBar action = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // ActionBar action = getSupportActionBar(); // get the actionbar

        if (isSearchOpened) { // test if the search is open
            // TODO: remove settingMenu.setVisible(false) if not needed in final release
            // settingMenu.setVisible(true); // show setting menu icon

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
            // TODO: remove settingMenu.setVisible(false) if not needed in final release
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
        userName = PreferenceData.getUserLoggedInName(getActivity());
        userEmail = PreferenceData.getLoggedInEmailUser(getActivity());
        userId = PreferenceData.getUserLoggedInId(getActivity());
        // Pass userId value to DataHolder
        DataHolder.setData(String.valueOf(userId));
        //TODO: This function is for storing login state and storing userId
        PreferenceData.getUserLoggedInStatus(getActivity());
        // For set user loggedin status
        PreferenceData.setUserLoggedInStatus(getActivity(), true, userId, userEmail, userName);
        // Get User Logged In status . true = login

        // Populate invitations arrayList
        invitations = myDb.fetchInvitationsRecieve(DataHolder.getData().toString());
        adapterWithFilter = new InvitationAdapterWithFilter(getActivity(), invitations);

        listView = view.findViewById(R.id.lvInvitations_fragment_home);
        lvInvitations.setAdapter(adapterWithFilter);


        lvInvitations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //** Find TextView value using id

                scrollPosition = position;
                TextView textView_ticketId = view.findViewById(R.id.textView_invitation_ticketId);
                TextView textView_senderId = view.findViewById(R.id.textView_invitation_senderId);
                TextView textView_eventAddress = view.findViewById(R.id.textView_vicinity);
                TextView textView_senderName = view.findViewById(R.id.textView_sender);
                TextView textView_status = view.findViewById(R.id.textView_invitation_status);
                TextView textView_eventTitle = view.findViewById(R.id.eventTitle);
                TextView textView_eventDate = view.findViewById(R.id.eventDate);
                TextView textView_evenStart = view.findViewById(R.id.eventStart);
                TextView textView_eventEnd = view.findViewById(R.id.eventEnd);
                TextView textView_eventRemarks = view.findViewById(R.id.eventRemarks);


                // Get textView value then pass into string variable
                requestStatus = textView_status.getText().toString();
                eventAddress = textView_eventAddress.getText().toString();
                senderName = textView_senderName.getText().toString();
                senderId = textView_senderId.getText().toString();
                ticketId = textView_ticketId.getText().toString();
                eventTitle = textView_eventTitle.getText().toString();
                eventDate = textView_eventDate.getText().toString();
                eventStart = textView_evenStart.getText().toString();
                eventEnd = textView_eventEnd.getText().toString();
                eventRemarks = textView_eventRemarks.getText().toString();


                String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date()); // Date today

                // Calculate the difference from Current date today to the date of the event
                GetCountOfDays getCountOfDays = new GetCountOfDays();
                diffOfDays = getCountOfDays.getCountOfDays(today, eventDate);

                /**
                 * TODO: For testing as of 3/7/2018. This code show custom dialog box
                 * This code is a separate class for accepting/reject request
                 * */
                // Call custom dialog box
                alertDialogInfoEvent.getTicketInfo(Integer.parseInt(ticketId), senderName,
                        requestStatus, eventDate, eventStart,
                        eventEnd, eventTitle, eventRemarks, eventAddress, diffOfDays);


                // save index and top position
                int index = lvInvitations.getFirstVisiblePosition();
                View v = lvInvitations.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - lvInvitations.getPaddingTop());
                // restore index and position
                alertDialogInfoEvent.itemPosition(index, top);

                // Show alert dialog
                alertDialogInfoEvent.showDialog(getActivity(), "");


            }


        });


    }


    //** Create Custom AlertDialog with ListView
    public void popUp(String ticket_id) {

        //** Fetch all user to ArrayList by ticket_id
        invited = myDb.fetchInvitationsDetailsObj(ticket_id);

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.alertdialog_listview_layout);
        dialog.setTitle("People Invited");
        ListView listView = dialog.findViewById(R.id.list);
        //** Create the adapter to convert the array to views
        AlertDialogAdapter adapter = new AlertDialogAdapter(getActivity(), 0, invited);
        //** Attach the adapter to a ListView
        listView.setAdapter(adapter);

        //** ListView Item listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //** Close dialog when click
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
