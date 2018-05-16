package com.xperiasola.philubiq64wi.navigationdrawerapp.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.activities.PersonalInfo;
import com.xperiasola.philubiq64wi.navigationdrawerapp.activities.SignIn;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.PreferenceData;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                //** Call function
                showDynamicMessage("", "Logout?", true);
                break;

            case R.id.nav_subcategory_personal: //** Personal info sub menu action bar
                Intent openPersonalInfo = new Intent(getActivity(), PersonalInfo.class);
                startActivity(openPersonalInfo);
                break;

            case R.id.nav_subcategory_appVersion://** App version menu action bar
                //** Call function
                showDynamicMessage("App version: ", "Beta v1.0.1", false);
                break;
            case R.id.action_logout: //** Logout sub menu action bar
                showDynamicMessage("", "Logout?", true);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;


    }

    //** Show AlertDialog message for Logout and App version menu
    public void showDynamicMessage(String title, String content, boolean isButtonNeed) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setCancelable(true);
        if (isButtonNeed) { //** AlertDialog for Logout user
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceData.clearLoggedInEmailAddress(getActivity()); // Clear SharePreference data when log out
                    Intent openSignInLayout = new Intent(getActivity(), SignIn.class);
                    openSignInLayout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //** Clear Activity
                    startActivity(openSignInLayout); //** Open SignIn Activity
                    getActivity().finish(); //** End Activity
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        builder.show();
    }


}
