package com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customButtomDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.interfaces.NavigationDrawerInterface;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by philUbiq64wi on 3/5/2018.
 * This class show custome bottom dialog for setting
 * event title,time,date and remarks
 */

public class BottomDialogEventInfo extends BottomSheetDialog {
    private BottomSheetDialog mBottomSheetDialogEventInfo;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RelativeLayout rTitle, rRemarks, rStartTime, rEndTime, rDate;
    private TextView textViewStartTime, textViewEndTime, textViewDate, textViewRemarks, textViewTitle;
    private Button btnSubmit;
    private String markerTitle, markerSnippet;
    private Switch swithEvent;
    private boolean isAdvaceActive = true;
    private Calendar calendar;
    private ImageView dismissBottomDialog;
    private static String latestTicketId;
    private InvitationModel invitation_model;
    private static DatabaseHelper MY_DB;
    private static ArrayList<Object> OBJECTS;
    private Activity activity;



    public BottomDialogEventInfo(Context context) {
        super(context);

    }

    public void dateTimeListenerView() {
        // Set view
        View view = this.getLayoutInflater().inflate(R.layout.custom_bottom_dialog_event_info, null);


        // Initialize database connection
        MY_DB = new DatabaseHelper(getContext());

        // Initialize model
        invitation_model = new InvitationModel();

        // Initialize Layouts Id
        rTitle = view.findViewById(R.id.layoutTitle);
        rRemarks = view.findViewById(R.id.layoutRemarks);
        rDate = view.findViewById(R.id.layoutDate);
        rStartTime = view.findViewById(R.id.layoutStart);
        rEndTime = view.findViewById(R.id.layoutEnd);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Initialize switch widget id
        swithEvent = view.findViewById(R.id.event_switch);

        // Initialize image view id
        dismissBottomDialog = view.findViewById(R.id.icon_dismiss);

        // Initialize TextViews Id
        textViewTitle = view.findViewById(R.id.txtTitle);
        textViewDate = view.findViewById(R.id.txtDate);
        textViewStartTime = view.findViewById(R.id.txtStartTime);
        textViewEndTime = view.findViewById(R.id.txtEndTime);
        textViewRemarks = view.findViewById(R.id.txtRemarks);

        // Set Done instead of enter in keyboard
        textViewDate.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textViewRemarks.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //Call function
        fetchDate();
        fetchTime();

        // Set listener on Close icon
        dismissBottomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialogEventInfo.dismiss();
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


        swithEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set model getters
                invitation_model.setPlace(markerTitle);
                invitation_model.setAddress(markerSnippet);
                invitation_model.setEventTitle(textViewTitle.getText().toString());
                invitation_model.setEventDate(textViewDate.getText().toString());
                invitation_model.setEventStart(textViewStartTime.getText().toString());
                invitation_model.setEventEnd(textViewEndTime.getText().toString());
                invitation_model.setEventRemarks(textViewRemarks.getText().toString());

                // Insert data into table
                boolean isInserted = MY_DB.insertInvitationTicketBeta(markerTitle, markerSnippet,
                        invitation_model.getEventTitle(), invitation_model.getEventDate(),
                        invitation_model.getEventStart(), invitation_model.getEventEnd(),
                        invitation_model.getEventRemarks(), isAdvaceActive
                );
                if (isInserted == true) {
                    // Call function
                    fetchSelectedContactList();
                    mBottomSheetDialogEventInfo.dismiss();

                    /**
                     * Redirect user to sent invitation list after sending invite
                     * by calling method into main activity using interface.*/
                    ((NavigationDrawerInterface) activity).showSentInvitation();

                    Toast.makeText(getContext(), "Action: Invitation Sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Action: Place Failed", Toast.LENGTH_SHORT).show();

                }


            }
        });


        if (view.getParent() == null) {
            mBottomSheetDialogEventInfo = new BottomSheetDialog(getContext());
            mBottomSheetDialogEventInfo.setContentView(view); // Set content view
            // Prevent from closing dialog when touch outside
            mBottomSheetDialogEventInfo.setCanceledOnTouchOutside(false);
            mBottomSheetDialogEventInfo.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
            mBehavior.setPeekHeight(1000); // Set peekHeight to fully show the bottom dialog
            mBottomSheetDialogEventInfo.show(); // Show bottom dialog
        } else {
            mBottomSheetDialogEventInfo.setContentView(view); // Set content view
            // Prevent from closing dialog when touch outside
            mBottomSheetDialogEventInfo.setCanceledOnTouchOutside(false);
            mBottomSheetDialogEventInfo.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
            mBehavior.setPeekHeight(1000); // Set peekHeight to fully show the bottom dialog
            mBottomSheetDialogEventInfo.show(); // Show bottom dialog
        }


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

    // Set 1 hour advance from the system current time
    public void fetchTime() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY) + 1; // Add 1 hour
        mMinute = c.get(Calendar.MINUTE);

        textViewStartTime.setText(formatTime(mHour, mMinute));
        textViewEndTime.setText(formatTime(mHour, mMinute));

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
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        // Show keyboard when alertDialog create
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final EditText input = new EditText(getContext());
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

    // Get marker title and snippet information from MapViewFragment Activity
    public void fetchMarkerInfo(String title, String snippet) {
        markerTitle = title;
        markerSnippet = snippet;
    }

    // Get selected contact list
    public void getUserList(ArrayList<Object> getObject) {
        OBJECTS = getObject;
        System.out.println("Object: " + OBJECTS.toString());
    }

    /**
     * Get last ticket id inserted into table
     * then insert all selected contact
     */
    public static void fetchSelectedContactList() {

        //** Get last ticket_id
        Cursor cursorResult = MY_DB.getLastInvitaionTicket();
        while (cursorResult.moveToNext()) {
            latestTicketId = cursorResult.getString(0); //* Get Column Id value in table invitation_ticket
        }

        //** Insert all contact selected
        for (int x = 0; x < OBJECTS.size(); x++) {
//            boolean isInserted = MY_DB.insertInvitationData((String) OBJECTS.get(x), latestTicketId);
            boolean isInserted = MY_DB.insertInvitationData((String) OBJECTS.get(x), latestTicketId);
            if (isInserted == true) {
                //** Inserted Successfully
            } else {
                //** Inserted Failed
            }
        }


    }

    public void getActivityContext(Activity activity) {
               this.activity = activity;
    }


}
