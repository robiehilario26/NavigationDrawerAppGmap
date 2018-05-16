package com.xperiasola.philubiq64wi.navigationdrawerapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.adapter.UsersAdapter;
import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.UserModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


    DatabaseHelper myDb;
    private static ListView lvContacts;
    private ArrayList<UserModel> contacts;
    private Button bSendInvite;
    private CheckBox checkBox_contact_picker;
    private Bundle extra = new Bundle();
    private ArrayList<Object> objects;
    private View view;
    private static final String TAG_MAPS = "Map";
    public static String CURRENT_TAG = TAG_MAPS;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact, container, false);


        //** Initialize database connection
        myDb = new DatabaseHelper(getActivity());

        //** Initialize object
        lvContacts = view.findViewById(R.id.lvContacts);
        bSendInvite = view.findViewById(R.id.bSendInvite);
        checkBox_contact_picker = view.findViewById(R.id.checkboxAgreement);

        //** Call function
        populateUsersList();
        getCheckedContact();


        return view;
    }


    private void populateUsersList() {
        //** Construct the data source
        //** Create the adapter to convert the array to views
        contacts = myDb.fetchContacts(DataHolder.getData().toString()); //** Get current user Id

        //** Call customized ArrayAdapter
        UsersAdapter adapter = new UsersAdapter(getActivity(), 0, contacts);
        //** Attach the adapter to a ListView
        ListView listView = view.findViewById(R.id.lvContacts);
        listView.setAdapter(adapter);

        //** TODO: Remove this Item listener in the future if no added features will create in Contacts Activity
        //** Set Item listener
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //** Get TextView value using id
                TextView textView_userId = view.findViewById(R.id.textView_userId);
                String userId = textView_userId.getText().toString();
                Toast.makeText(getActivity(), "ID: " + userId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //** Get checked contact
    public void getCheckedContact() {
        bSendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListView mainListView = view.findViewById(R.id.lvContacts);
                //** Create ArrayList Object
                objects = new ArrayList<>();
                StringBuffer buffer = new StringBuffer();

                //** Get all ListView Contacts child
                for (int x = 0; x < mainListView.getChildCount(); x++) {
                    checkBox_contact_picker = mainListView.getChildAt(x).findViewById(R.id.checkboxAgreement);
                    //** Get checked box userId value
                    if (checkBox_contact_picker.isChecked()) { //** Item is Checked

                        TextView textView_userId = mainListView.getChildAt(x).findViewById(R.id.textView_userId); //** Get textView userID value
                        TextView textView_fullName = mainListView.getChildAt(x).findViewById(R.id.textView_contact_fullname); //** Get textView fullname value
                        String userId = textView_userId.getText().toString(); //** Convert to string
                        String fullName = textView_fullName.getText().toString(); //** Convert to string
                        buffer.append("id: " + userId + "\n");
                        buffer.append("fullname: " + fullName + "\n");
                        objects.add(userId); //** Add Contact Id value to Object ArrayList


                    }

                }

                //** Get all Array Object

                Fragment fragment = new MapViewFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                extra.putSerializable("objects", objects);
                fragment.setArguments(extra);

                //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                // android.R.anim.fade_out);

                //** Check Array Object length
                if (objects.size() > 0) { //** If there is Checked contact
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment,CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    //** No Contact Checked
                    Toast.makeText(getActivity(), "No Contact Selected", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

}
