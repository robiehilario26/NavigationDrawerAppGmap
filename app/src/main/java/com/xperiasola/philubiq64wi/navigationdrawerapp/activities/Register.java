package com.xperiasola.philubiq64wi.navigationdrawerapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.UserModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.services.RegisterAPI;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.makeText;

public class Register extends AppCompatActivity {
    private static Button bRegister;
    private static EditText etFullname, etAge, etPass, etEmail, etHint;
    private CheckBox checkboxRegisterAgreement;
    DatabaseHelper myDb;


    private static String email, name, age, password, hint;


    //initialize UserModel class
    UserModel user = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //** Show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //** Initialize my Database Connection Class
        myDb = new DatabaseHelper(Register.this);

        //** Initialize object id
        bRegister = findViewById(R.id.bRegister);
        checkboxRegisterAgreement = findViewById(R.id.checkboxRegisterAgreement);

        //** Initialize Edit Text object
        etEmail = findViewById(R.id.etEmail);
        etFullname = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etPass = findViewById(R.id.etPass);
        etHint = findViewById(R.id.etPassHint);

        //** Call function
        insertUserData();
        isCheckedListener();


    }

    //** Override back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //end activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //** insert all data into table_user_log
    public void insertUserData() {
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = etEmail.getText().toString();
                name = etFullname.getText().toString();
                age = etAge.getText().toString();
                password = etPass.getText().toString();
                hint = etHint.getText().toString();

                //** TODO: uncomment this to use RegisterAPI.class services
                //** Insert data on online database
                /*Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success == true) {
                                showMessage("Success", "Data inserted " );
                            } else {
                                showMessage("Failed", "Failed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showMessage("Failed", e.toString());
                        }
                    }
                };

                RegisterAPI registerAPI = new RegisterAPI(EMAIL, NAME, AGE, PASSWORD, responseListner);
                RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                requestQueue.add(registerAPI);*/

                //** Insert using UserModel class getters and setters
                user.setUser_email(etEmail.getText().toString());
                user.setUser_fullName(etFullname.getText().toString());
                user.setUser_age(etAge.getText().toString());
                user.setUser_password(etPass.getText().toString());
                user.setUser_hint(etHint.getText().toString());

                //** Validate TextView input
                boolean isValid = validatorListener(user.getUser_email().length(),
                        user.getUser_fullName().length(), user.getUser_age().length(),
                        user.getUser_password().length());
                boolean isValidEmail = isValidEmail(email);
                if (isValid == true && isValidEmail == true) {
                    //** When all TextView fill-up and email si valid
                    //** Call function
                    showMessage("", "Save?");
                } else {
                    //** When other TextView has no value and email is invalid
                    makeText(Register.this, "Invalid Email or incomplete detail", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    //** Checking if email is existed or not
    public void checkEmailListener() {
        //** Check if email is already exist
        boolean isExist = myDb.checkEmail(user);
        if (isExist == true) {
            makeText(Register.this, "Email Address already exist!!!", Toast.LENGTH_SHORT).show();
        } else {
            //** If email is not existed then insert
            registerUser();
        }
    }

    //** Insert data into Database
    public void registerUser() {
        //** Call insert function in DatabaseHelper
        boolean isInsert = myDb.insertUserData(user);
        //** Check return value of boolean
        if (isInsert == true) { //** Insert successfully
            makeText(Register.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
            Intent openSignInLayout = new Intent(Register.this, SignIn.class);
            openSignInLayout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(openSignInLayout);
            finish(); //** Finish activity
        } else {                       //** Failed insertion
            makeText(Register.this, "Data Inserted Failed", Toast.LENGTH_SHORT).show();
        }
    }


    //** Show alert dialog for Register
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setCancelable(true); // closable
        builder.setTitle(title); // set message title
        builder.setMessage(message); // set message content
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkEmailListener(); // save into SQLite phone memory only
                // TODO: uncomment this to use Register API, save into Database using Register.php API
                // registerAPI();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();// show alert box
    }

    //** Register into online database using Register.php
    //TODO: use this function to insert Data in 000WebHost Database
    public void registerAPI() {
        Response.Listener<String> responseListner = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success == true) {
                        makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //** Pass value to constructor
        RegisterAPI registerAPI = new RegisterAPI(email, name, age, password, responseListner);
        RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
        requestQueue.add(registerAPI);
    }


    //** Check if there is TextView field with no text inputted
    public boolean validatorListener(int email, int name, int age, int password) {
        if (email == 0 || name == 0 || age == 0 || password == 0) {
            return false;
        } else {
            return true;
        }

    }

    public void isCheckedListener() {
        // Set Register button to disabled
        bRegister.setEnabled(false);

        // Set checkbox listener
        checkboxRegisterAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // If checkbox is checked
                    bRegister.setEnabled(true); // Enable button
                } else {
                    bRegister.setEnabled(false); // Disable button
                }
            }
        });
    }

    // Validate email
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        // Using API 8 or above, you can use the readily available Patterns class to validate email.
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
