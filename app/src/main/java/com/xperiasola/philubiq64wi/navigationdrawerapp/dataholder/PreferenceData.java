package com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by philUbiq64wi on 2/26/2018.
 */

public class PreferenceData
{
    static final String PREF_LOGGEDIN_USER_NAME = "logged_in_name";
    static final String PREF_LOGGEDIN_USER_EMAIL = "logged_in_email";
    static final String PREF_USER_LOGGEDIN_STATUS = "logged_in_status";
    static final String PREF_USER_LOGGEDIN_ID = "0";

    public static SharedPreferences getSharedPreferences(Context ctx)
    {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // TODO: for future purposed if needed.
    public static void setLoggedInUserEmail(Context ctx, String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGGEDIN_USER_EMAIL, email);
        editor.commit();
    }

    // TODO: for future purposed if needed.
    public static String getLoggedInEmailUser(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_LOGGEDIN_USER_EMAIL, "");
    }

    public static void setUserLoggedInStatus(Context ctx, boolean status, int userId, String userEmail, String name)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_USER_LOGGEDIN_STATUS, status);
        editor.putInt(PREF_USER_LOGGEDIN_ID, userId);
        editor.putString(PREF_LOGGEDIN_USER_EMAIL, userEmail);
        editor.putString(PREF_LOGGEDIN_USER_NAME, name);
        editor.commit();
    }

    public static boolean getUserLoggedInStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_USER_LOGGEDIN_STATUS, false);
    }

    public static int getUserLoggedInId(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(PREF_USER_LOGGEDIN_ID, 0);
    }

    public static String getUserLoggedInName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_LOGGEDIN_USER_NAME, "");
    }


    // TODO: Clearing all preferences data when log out
    public static void clearLoggedInEmailAddress(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_LOGGEDIN_USER_EMAIL);
        editor.remove(PREF_USER_LOGGEDIN_STATUS);
        editor.remove(PREF_USER_LOGGEDIN_ID);
        editor.commit();
    }


}