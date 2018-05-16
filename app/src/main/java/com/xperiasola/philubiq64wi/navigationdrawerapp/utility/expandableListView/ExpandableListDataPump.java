package com.xperiasola.philubiq64wi.navigationdrawerapp.utility.expandableListView;

/**
 * Created by philUbiq64wi on 3/13/2018.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    private static ArrayList<String> address;
    private static ArrayList<String> remarks;

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        expandableListDetail.put("Location Address", address);
        expandableListDetail.put("Remarks", remarks);

        // TODO: For reference purposed
        // expandableListDetail.put("People Invited", personList);
        return expandableListDetail;
    }

    // TODO: For reference purposed
    // Fetch all invited people
    public void fethRemarks(List<String> list) {

        remarks = new ArrayList<>();
        for (int x = 0; x < list.size(); x++) {
            remarks.add(list.get(x));

        }

    }

    // Fetch location address
    public void fetchAddress(List<String> list) {

        address = new ArrayList<>();
        for (int x = 0; x < list.size(); x++) {
            address.add(list.get(x));
        }
    }
}