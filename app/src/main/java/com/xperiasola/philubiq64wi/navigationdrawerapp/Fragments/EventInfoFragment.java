package com.xperiasola.philubiq64wi.navigationdrawerapp.Fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EventInfoFragment extends Fragment {

    private Button btnDatePicker, btnTimePicker;
    private EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RelativeLayout rStartTime, rEndTime, rDate;
    private TextView textViewStartTime, textViewEndTime, textViewDate, textViewRemarks;

    public EventInfoFragment() {
        // Required empty public constructor
    }



// TODO: Delete this fragment when no further usage in final release
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_info, container, false);


        // Initialize Layouts Id
        rDate = view.findViewById(R.id.layoutDate);
        rStartTime = view.findViewById(R.id.layoutStart);
        rEndTime = view.findViewById(R.id.layoutEnd);

        // Initialize TextViews Id
        textViewDate = view.findViewById(R.id.txtDate);
        textViewStartTime = view.findViewById(R.id.txtStartTime);
        textViewEndTime = view.findViewById(R.id.txtEndTime);
        textViewRemarks = view.findViewById(R.id.txtRemarks);


        // Call function
        dateTimeListener();
        return view;
    }

    public void dateTimeListener() {

        // Set Done instead of enter in keyboard
        textViewDate.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textViewRemarks.setImeOptions(EditorInfo.IME_ACTION_DONE);

        rDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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

}
