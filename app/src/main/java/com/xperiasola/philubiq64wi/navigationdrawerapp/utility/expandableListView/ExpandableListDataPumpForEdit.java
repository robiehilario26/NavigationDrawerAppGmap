package com.xperiasola.philubiq64wi.navigationdrawerapp.utility.expandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by philUbiq64wi on 3/19/2018.
 */

public class ExpandableListDataPumpForEdit {

    private static ArrayList<String> person;

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();




        expandableListDetail.put("People Invited", person);

        // TODO: For reference purposed
        // expandableListDetail.put("People Invited", personList);
        return expandableListDetail;
    }

    // TODO: For reference purposed
    // Fetch all invited people
    public void fetchPeople(List<String> list) {

        person = new ArrayList<>();
        for (int x = 0; x < list.size(); x++) {
            person.add(list.get(x));
            System.out.println("item: "+ list.get(x));

        }

    }


}
