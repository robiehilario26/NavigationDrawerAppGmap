package com.xperiasola.philubiq64wi.navigationdrawerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;

public class SplashActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * This is the first activity to be open to find if user
         * is already login or not
         * eg: If user doest logout tha app will directly open
         * into default home screen.
         * But if user logout before closing the app and didn't login
         * the app will redirect the user to Login activity
         * */

        // Call function
        fetchPreferencesData();
    }

    // Fetch last user id who login
    public void fetchPreferencesData() {

        // TODO: For testing of sharePreference
        /**
         * userId = 0 The user logout
         * userId > 0 Retrieve the last User Id who login
         **/

        // Fetch userId using PreferenceData
        int userId = PreferenceData.getUserLoggedInId(SplashActivity.this);
        if (userId > 0) {
            intent = new Intent(this, CustomNavigationDrawer.class);
        } else {
            intent = new Intent(SplashActivity.this, SignIn.class);
        }
        startActivity(intent); // Open activity
        finish();

    }
}
