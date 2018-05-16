package com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder;

/**
 * Created by philUbiq64wi on 2/2/2018.
 */

public class DataHolder {

    //** Stored User Id for created and update purposes
    private static String data;

    public static String getData() {
        return data;
    }

    //** SetData to Static
    public static void setData(String data) {
        DataHolder.data = data;
    }


}
