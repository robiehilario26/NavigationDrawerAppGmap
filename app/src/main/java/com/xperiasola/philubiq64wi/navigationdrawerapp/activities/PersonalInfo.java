package com.xperiasola.philubiq64wi.navigationdrawerapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.UserModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;

public class PersonalInfo extends AppCompatActivity {

    private static EditText etFullname, etAge, etPass, etEmail, etHint;
    DatabaseHelper myDb;
    UserModel user = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //** Show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //** Initialize my Database Connection Class
        myDb = new DatabaseHelper(PersonalInfo.this);

        //** Initialize Edit Text object
        etFullname = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etPass = findViewById(R.id.etPass);
        etEmail = findViewById(R.id.etEmail);
        etHint = findViewById(R.id.etPassHint);

        //** Set TextView to readonly
        etEmail.setKeyListener(null);

        //** Call function
        loadUserInfo(); //** Fetch user info

    }

    //** Fetch user info
    public void loadUserInfo() {
        //** Initialize UserModel class
        UserModel user = new UserModel();
        user.setUser_id(DataHolder.getData().toString()); //** Get session Id
        Cursor cursorResult = myDb.loadPersonalInfo(user);

        while (cursorResult.moveToNext()) {
            etEmail.setText(cursorResult.getString(1)); // COLUMN email
            etFullname.setText(cursorResult.getString(2)); // COLUMN fullname
            etAge.setText(cursorResult.getString(3)); // COLUMN age
            etPass.setText(cursorResult.getString(4)); // COLUMN password
            etHint.setText(cursorResult.getString(5)); // COLUMN password hint
        }
        cursorResult.close(); //** Close


    }

    //** Display custom menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_personal_activity, menu); //** Menu bar design layout
        return super.onCreateOptionsMenu(menu);
    }


    //** Set save button listener in action menu bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.personal_menu_save_id) {

            isKeyboardActive(); // Call function for checking if keyboard is open or not
            //** Validate TextView input
            boolean isValid = validatorListener(etEmail.length(), etFullname.length(), etAge.length(), etPass.length());
            if (isValid == true) {
                //** When all TextView fill-up
                showMessage("Save Changes?");
            } else {
                //** When other TextView has no value
                Toast.makeText(PersonalInfo.this, "Incomplete Detail", Toast.LENGTH_SHORT).show();
            }

        } else if (id == android.R.id.home) {
            //** End activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //** Show AlertDialog yes/no for updating user information
    public void showMessage(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // Yes button listener
            @Override
            public void onClick(DialogInterface dialog, int which) { // Yes button listener


                //** Call update function
                updateUserInfo(DataHolder.getData().toString(), user); //** updateUserInfo("get static id of user","pass user model object")
                Toast.makeText(PersonalInfo.this, "Action: Saved!!!", Toast.LENGTH_SHORT).show(); //** Show Toast message
                Intent invitationLayout = new Intent(PersonalInfo.this, CustomNavigationDrawer.class); //** Back to Navigator home page
                invitationLayout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //** Clear Activity before opening other new activity
                startActivity(invitationLayout); //** Open activity
                finish(); //** Clear activity

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //** Do nothing
                dialog.cancel();
            }
        });
        builder.show();


    }

    //** Get user info for updating
    public void updateUserInfo(String sessionId, UserModel user) {
        //** Set UserModel user value
        user.setUser_id(sessionId);
        user.setUser_email(etEmail.getText().toString());
        user.setUser_fullName(etFullname.getText().toString());
        user.setUser_age(etAge.getText().toString());
        user.setUser_password(etPass.getText().toString());
        user.setUser_hint(etHint.getText().toString());


        //** Call DatabaseHelper update method
        boolean isUpdated = myDb.updateLoginUser(user);

        //** Check boolean value true/false
        if (isUpdated == true) {
            //** Update Successfully
            Toast.makeText(this, "Action: Update Successfully!!!", Toast.LENGTH_SHORT).show();
        } else {
            //** Update Failed
            Toast.makeText(this, "Action: Update Failed!!!", Toast.LENGTH_SHORT).show();
        }
    }

    //** Check if there is TextView field that not properly input data
    public boolean validatorListener(int email, int name, int age, int password) {
        if (email == 0 || name == 0 || age == 0 || password == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void isKeyboardActive(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Error",""+e.toString());
        }
    }


}
