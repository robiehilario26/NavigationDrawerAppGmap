package com.xperiasola.philubiq64wi.navigationdrawerapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.google_map_activities.NearByPlaces;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.EventInfoModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customAlertDialog.CustomDialogSentInfoClass;

import java.util.Calendar;
import java.util.Date;

public class EditInvitation extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute, ticketId;
    private RelativeLayout rTitle, rRemarks, rStartTime, rEndTime, rDate, rEventAddress;
    private TextView textViewStartTime, textViewEndTime,
            textViewDate, textViewRemarks, textViewTitle,
            textViewVicinity, textViewActivityName;
    private Switch switchEvent;
    private boolean isAdvaceActive;
    private Calendar calendar;

    private InvitationModel invitation_model;
    private DatabaseHelper myDb;
    private EventInfoModel eventInfoModel;
    private String finalTitle, finalPlaceName, finalDate,
            finalStart, finalEnd, finalRemarks, finalPlace;
    private Boolean isAdvance;
    private CustomDialogSentInfoClass customDialogSentInfoClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_invitation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize database and model
        myDb = new DatabaseHelper(EditInvitation.this); // Database
        invitation_model = new InvitationModel(); // Mode class

        // Initialize Layouts Id
        rTitle = findViewById(R.id.layoutTitle);
        rRemarks = findViewById(R.id.layoutRemarks);
        rDate = findViewById(R.id.layoutDate);
        rStartTime = findViewById(R.id.layoutStart);
        rEndTime = findViewById(R.id.layoutEnd);
        rEventAddress = findViewById(R.id.LayoutEventAddress);

        // Initialize switch widget id
        switchEvent = findViewById(R.id.event_switch);

        // Initialize TextViews Id
        textViewActivityName = findViewById(R.id.textViewActivityName);
        textViewVicinity = findViewById(R.id.textView_vicinity);
        textViewTitle = findViewById(R.id.txtTitle);
        textViewDate = findViewById(R.id.txtDate);
        textViewStartTime = findViewById(R.id.txtStartTime);
        textViewEndTime = findViewById(R.id.txtEndTime);
        textViewRemarks = findViewById(R.id.txtRemarks);

        // Set Done instead of enter in keyboard
        textViewDate.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textViewRemarks.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //** Show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Call function
        getParcelableExtra();


        // Set Event address click listener
        rEventAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // EventInfoModel Parcelable class
                EventInfoModel dataToSend = new EventInfoModel(finalTitle, finalDate, finalStart,
                        finalEnd, finalRemarks, finalPlace, isAdvance, finalPlaceName, ticketId);
                Intent intent = new Intent(EditInvitation.this, NearByPlaces.class);
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("myDataKey", dataToSend); // Pass value
                startActivity(intent);

            }
        });


        // Set Title click listener
        rTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get textView title current value
                String data = textViewTitle.getText().toString();
                // Call function
                createCustomDialog("Event Title", "Enter event title.", data, textViewTitle);
            }
        });


        switchEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("Switch state: " + isChecked);

                isAdvaceActive = isChecked;
                // Call function
                fetchDate();
            }
        });

        // Set remarks click listener
        rRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get textView remarks current value
                String data = textViewRemarks.getText().toString();
                // Call function
                createCustomDialog("Event Remarks", "Enter remarks.", data, textViewRemarks);
            }
        });

        rDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Current Date
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                if (isAdvaceActive) {
                    c.add(Calendar.DAY_OF_MONTH, +2); // Add 2 day to the current date
                } else {
                    c.add(Calendar.DAY_OF_MONTH, 0); // Add nothing
                }


                Date result = c.getTime();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditInvitation.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                /**
                                 * Convert day of month if less than 10
                                 * eg: 10-1-2018 to 10-01-2018
                                 * */
                                String dayOfMonthConverted = "" + dayOfMonth;
                                if (dayOfMonth < 10) {
                                    dayOfMonthConverted = "0" + dayOfMonthConverted;
                                }
                                textViewDate.setText((monthOfYear + 1) + "/" + dayOfMonthConverted + "/" + year);

                            }
                        }, mYear, mMonth, mDay);


                /**
                 * Set minimum date into advance 2 days
                 * e.i current date: 3/12/2018
                 * minimum date selected: 3/14/2018
                 * */
                datePickerDialog.getDatePicker().setMinDate(result.getTime());
                datePickerDialog.show();

            }


        });


        rStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditInvitation.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                /**
                                 * Call function formatTime(Pass hourOfDay and minute integer value)
                                 * Format time into 12 hour format
                                 * */
                                textViewStartTime.setText(formatTime(hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        rEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditInvitation.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                /**
                                 * Call function formatTime(Pass hourOfDay and minute integer value)
                                 * Format time into 12 hour format
                                 * */
                                textViewEndTime.setText(formatTime(hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

    }


    // Create alertDialog with editText
    public void createCustomDialog(String title, String message, String inputData, final TextView textView) {
        // Get textView title current value
        String data = inputData;
        // If data is None set to blank
        if (data.equalsIgnoreCase("none")) {
            data = "";
        }

        // Create alert dialog with edit text inside
        final AlertDialog alertDialog = new AlertDialog.Builder(EditInvitation.this).create();
        // Show keyboard when alertDialog create
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final EditText input = new EditText(EditInvitation.this);
        alertDialog.setTitle(title); // set alertDialog title
        alertDialog.setMessage(message); // set alertDialog message
        input.setImeOptions(EditorInfo.IME_ACTION_DONE); // set keyboard editor to Done
        input.setSingleLine(true); // Set to true to make IME_ACTION_DONE work
        alertDialog.setView(input); // Set view
        alertDialog.setCancelable(false);
        // Set input text value
        input.setText(data);

        int position = input.length(); // get input text length
        Editable editable = input.getText(); // get input text value
        Selection.setSelection(editable, position); // set cursor focus to end of text


        // Set input text listener
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If user press done on keyboard set value of textViewTitle
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Set textViewTitle value
                    textView.setText(input.getText().toString()); // Set value base on input
                    alertDialog.dismiss(); // alertDialog dismiss
                }
                return false;
            }
        });


        alertDialog.show();
    }


    // Change default date when switch is toggle on/off
    // Set default date to system current date
    public void fetchDate() {


        // Get Current Date
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (isAdvaceActive == true) {
            mDay = calendar.get(Calendar.DAY_OF_MONTH) + 2; // Add 2 day to the current date
        } else {
            mDay = calendar.get(Calendar.DAY_OF_MONTH); // Add nothing
        }

        /**
         * Convert day of month if less than 10
         * eg: 10-1-2018 to 10-01-2018
         * */
        String dayOfMonthConverted = "" + mDay;
        if (mDay < 10) {
            dayOfMonthConverted = "0" + dayOfMonthConverted;
        }
        textViewDate.setText((mMonth + 1) + "/" + dayOfMonthConverted + "/" + mYear);
    }


    // Set actual time in 12 hour format
    public static StringBuilder formatTime(int hour, int minute) {
        StringBuilder stringBuilder = new StringBuilder();


        if (hour >= 12) { // If hour is less than 12 hour
            stringBuilder.append(hour - 12).append(":"); // Hour
            stringBuilder.append(convertMinute(minute)).append(" "); // Minute
            stringBuilder.append(timeSet(hour)); // AM or PM
        } else {  // If hour is greater than 12 hour
            stringBuilder.append(hour).append(":"); // Hour
            stringBuilder.append(convertMinute(minute)).append(" "); // Minute
            stringBuilder.append(timeSet(hour)); // AM or PM
        }

        return stringBuilder;
    }

    // Set hour time AM or PM
    public static String timeSet(int hourOfDay) {
        String AM_PM;
        // If hour is less than 12 set to AM
        if (hourOfDay < 12) {
            AM_PM = "AM";
        } else {
            // If hour is more than 12 set to PM
            AM_PM = "PM";
        }
        return AM_PM;
    }


    /**
     * Get minute integer value)
     * Convert minute if less than 10
     * eg: 11:5 am to 11:05 am
     */
    public static String convertMinute(int minute) {
        String minuteConverted = "" + minute;
        if (minute < 10) {
            minuteConverted = "0" + minuteConverted;
        }
        return minuteConverted.toString();
    }


    // Get parcelable extra from previous activity
    public void getParcelableExtra() {
        //collect our intent
        Intent intent = getIntent();
        eventInfoModel = intent.getParcelableExtra("myDataKey");

        // Set textView value
        finalPlaceName = eventInfoModel.getPlaceName();
        finalPlace = eventInfoModel.getPlace();
        finalTitle = eventInfoModel.getEventTitle();
        finalDate = eventInfoModel.getEventDate();
        finalStart = eventInfoModel.getEventStart();
        finalEnd = eventInfoModel.getEventEnd();
        finalRemarks = eventInfoModel.getEventRemarks();
        isAdvance = eventInfoModel.getAdvance();
        ticketId = eventInfoModel.getTicketId();

        // Set string value
        textViewActivityName.setText(finalPlaceName);
        textViewVicinity.setText(finalPlace);
        textViewTitle.setText(finalTitle);
        textViewDate.setText(finalDate);
        textViewStartTime.setText(finalStart);
        textViewEndTime.setText(finalEnd);
        textViewRemarks.setText(finalRemarks);
        switchEvent.setChecked(isAdvance);


    }

    // Display custom menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_personal_activity, menu); //** Menu bar design layout
        return super.onCreateOptionsMenu(menu);
    }

    // Set save button listener in action menu bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Initialize utility class
        customDialogSentInfoClass = new CustomDialogSentInfoClass();

        // Click listener for save button
        if (id == R.id.personal_menu_save_id) {

            showMessage("Confirmation", "Save changes?");


        } else if (id == android.R.id.home) {
            // End activity
            customDialogSentInfoClass.checkEditState(false);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    // Set object data value
    public void fetchObjectData() {
        // Set state
        customDialogSentInfoClass.checkEditState(true);

        // Initialize model class
        InvitationModel invitationModel = new InvitationModel();

        // Setters value of each model
        invitationModel.setPlaceName(textViewActivityName.getText().toString());
        invitationModel.setPlace(textViewVicinity.getText().toString());
        invitationModel.setEventTitle(textViewTitle.getText().toString());
        invitationModel.setEventDate(textViewDate.getText().toString());
        invitationModel.setEventStart(textViewStartTime.getText().toString());
        invitationModel.setEventEnd(textViewEndTime.getText().toString());
        invitationModel.setEventRemarks(textViewRemarks.getText().toString());
        invitationModel.setEventAdvance(isAdvaceActive);
        invitationModel.setTicket_id(String.valueOf(ticketId));

        // Check update state
        boolean isUpdate = myDb.updateEventInfo(invitationModel);
        if (isUpdate) {
            // If inserted successfully
            Toast.makeText(this, "Success ", Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            // Failed to insert data
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }


    public void showMessage(String title, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchObjectData();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }


    // TODO: Remove this code when no further usage in final release
//    public void populateExpandedListViewInfo() {
//
//        List<String> people = new ArrayList<>();
//
//        people.add("Header 1");
//        people.add("Header 2");
//        people.add("Header 3");
//        people.add("Header 4");
//
//
//
//        // TODO: For testing. Expandable list view
//        ExpandableListView expandableListView;
//        ExpandableListAdapter expandableListAdapter;
//        final List<String> expandableListTitle;
//        final HashMap<String, List<String>> expandableListDetail;
//
//
//        // TODO: For testing. Expandable list view
//        expandableListView = findViewById(R.id.expandableListView_edit);
//
//        // Fetch invited person then put into List<String>
//
//        ExpandableListDataPumpForEdit expandableListDataPumpForEdit = new ExpandableListDataPumpForEdit();
//        expandableListDataPumpForEdit.fetchPeople(people); // Set invited
//
//
//        expandableListDetail = expandableListDataPumpForEdit.getData();
//        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
//        expandableListAdapter = new CustomExpandableListEditAdapter(EditInvitation.this,
//                expandableListTitle, expandableListDetail);
//        expandableListView.setAdapter(expandableListAdapter);
//
//        // Set listener when expandedListView expand
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                // TODO: for future reference
//                /*Toast.makeText(activity,
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();*/
//            }
//        });
//
//        // Set listener when expandedListView collapse
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                // TODO: for future reference
//                /*Toast.makeText(activity,
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();*/
//
//            }
//        });
//
//        // Set listener when clicking on child item inside expanded ListView
//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                // TODO: for future reference
//               /* Toast.makeText(
//                        activity,
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                ).show();*/
//                return false;
//            }
//        });
//    }
}
