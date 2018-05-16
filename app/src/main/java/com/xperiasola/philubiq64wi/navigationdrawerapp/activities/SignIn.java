package com.xperiasola.philubiq64wi.navigationdrawerapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.UserModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.services.LoginAPI;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;

import org.json.JSONException;
import org.json.JSONObject;

public class SignIn extends AppCompatActivity {
    private static Button bSignIn;
    private static EditText etUser, etPassword;
    private TextView textViewHint;
    private String fullName, userEmail, hint;
    DatabaseHelper myDb;
    UserModel user = new UserModel();    //** Initialize UserModel Class Object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //** Initialize Database Connection
        myDb = new DatabaseHelper(SignIn.this);

        //** Initialize Button object
        bSignIn = findViewById(R.id.bSignIn);

        //** Initialize Edit Text object
        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        textViewHint = findViewById(R.id.textViewHint);

        //** Call function
        onRegisterListener();
        fetchPasswordHint();

    }

    //** Get user name and password length
    public void onRegisterListener() {


        //** Set button SignIn onClick listener
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int get_etUser = etUser.getText().length(); //** Get username text field length
                int get_etPass = etPassword.getText().length(); //** Get password text field length

                if (get_etUser == 0 && get_etPass == 0) { //** If length of user and password is 0
                    Toast.makeText(SignIn.this, "Register Mode", Toast.LENGTH_SHORT).show();
                    Intent openRegisterLayout = new Intent(SignIn.this, Register.class);//** Call Register class
                    startActivity(openRegisterLayout); //** Open Register Layout
                } else {
                    //** If length of user and password is more than 0
                    //** Setters value of User email and password
                    user.setUser_email(etUser.getText().toString());
                    user.setUser_password(etPassword.getText().toString());

                    //** TODO: uncomment this to use LoginAPI, select into Database using Login.php API
                    //** LoginAPI(user.getUser_email(), user.getUser_password()); // login into online database

                    //** Call function
                    signInListener(); //** Login into local database inside phone memory

                }

            }
        });


    }


    //** For Login into local database only
    public void signInListener() {

        //** Call cursor function LoginUser
        Cursor cursorResult = myDb.loginUser(user);
        if (cursorResult.getCount() == 0) {
            //** No Data Found
            //** Show toast message
            Toast.makeText(SignIn.this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
            cursorResult.close();
        } else {

            StringBuffer buffer = new StringBuffer();
            while (cursorResult.moveToNext()) {

                //** Other are commented for easily detect the column index
                user.setUser_id(cursorResult.getString(0)); // Pass User Id value to UserModel Class
                // buffer.append("User ID: " + cursorResult.getString(0) + "\n"); // ID
                //  buffer.append("Email: " + cursorResult.getString(1) + "\n"); // EMAIL
                buffer.append(cursorResult.getString(2) + "\n"); // FULLNAME
                // buffer.append("Age: " + cursorResult.getString(3) + "\n"); //AGE
                // buffer.append("Password : " + cursorResult.getString(4) + "\n"); //PASSWORD

                // Pass data to string
                userEmail = cursorResult.getString(1);
                fullName = cursorResult.getString(2);

            }

            //** Show toast message if successfully Login
            Toast.makeText(SignIn.this, "Welcome: " + buffer.toString(), Toast.LENGTH_SHORT).show();
            Intent invitationLayout = new Intent(SignIn.this, CustomNavigationDrawer.class); //** Open Fragment Layout
            invitationLayout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //** Clear Activity
            DataHolder.setData(user.getUser_id());
            // For set user loggedin status
            PreferenceData.setUserLoggedInStatus(this, true, Integer.parseInt(user.getUser_id()), userEmail, fullName);
            startActivity(invitationLayout);
            cursorResult.close();
            finish();

        }
        cursorResult.close();
    }

    //** TODO: Use this function when Login into app using online database
    //** Login using LoginAPI services into online database
    public void loginAPI(String email, String password) {
        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    //** Check boolean return value
                    if (success == true) {
                        //** When successfully login
                        String name = jsonResponse.getString("name");
                        String age = jsonResponse.getString("age");
                        String email = jsonResponse.getString("email");
                        String password = jsonResponse.getString("password");

                        //** Show Success Login toast message
                        Toast.makeText(SignIn.this, "Success Login " + name, Toast.LENGTH_SHORT).show();
                    } else {
                        //** Show Failed Login toast message
                        Toast.makeText(SignIn.this, "Failed Login", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //** Pass value to constructor
        LoginAPI loginAPI = new LoginAPI(email, password, responseListner);
        RequestQueue requestQueue = Volley.newRequestQueue(SignIn.this);
        requestQueue.add(loginAPI);
    }

    // TODO: for future purposes
    // Toast hint for password for future purposed
    public void fetchPasswordHint() {
        textViewHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(SignIn.this);
                alert.setTitle("Forgot password");
                alert.setMessage("Enter email address");
                // Set an EditText view to get user input
                final EditText input = new EditText(SignIn.this);
                // Set input type of keyboard to include @ and .com
                input.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                alert.setView(input);

                // Set Ok button listener
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        user.setUser_email(input.getText().toString());
                        Cursor cursor = myDb.fetchPasswordHint(user);
                        if (cursor.getCount() == 0) {
                            // No result
                            Toast.makeText(SignIn.this, "Email not registered", Toast.LENGTH_SHORT).show();
                        } else {
                            while (cursor.moveToNext()) {
                                hint = cursor.getString(5); // Password hint
                            }
                            createAlertDialog(hint); // Call function
                        }
                        cursor.close();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();


            }
        });
    }

    // Show dialog for password hint
    public void createAlertDialog(String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignIn.this);
        alertBuilder.setTitle("Password hint");
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        // Add ok button to dismiss the dialog
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.show();
    }

    @Override
    protected void onDestroy() {
        myDb.close();
        super.onDestroy();
    }
}
