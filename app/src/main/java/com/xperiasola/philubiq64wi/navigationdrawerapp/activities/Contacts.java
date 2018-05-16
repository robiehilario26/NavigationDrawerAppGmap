package com.xperiasola.philubiq64wi.navigationdrawerapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
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

public class Contacts extends AppCompatActivity {
    String SessionId;
    DatabaseHelper myDb;
    private static ListView lvContacts;
    private ArrayList<UserModel> contacts;
    private Button bSendInvite;
    private CheckBox checkBox_contact_picker;
    Bundle extra = new Bundle();
    ArrayList<Object> objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatcs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //** Show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //** Initialize database connection
        myDb = new DatabaseHelper(this);

        //** Initialize object
        lvContacts = findViewById(R.id.lvContacts);
        bSendInvite = findViewById(R.id.bSendInvite);
        checkBox_contact_picker = findViewById(R.id.checkboxAgreement);

        //** Call function
        populateUsersList();
        getCheckedContact();


    }

    //** Override back button action listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //end activity
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

     private void populateUsersList() {
        //** Construct the data source
        //** Create the adapter to convert the array to views
        contacts = myDb.fetchContacts(DataHolder.getData().toString()); //** Get current user Id

         //** Call customized ArrayAdapter
        UsersAdapter adapter = new UsersAdapter(Contacts.this, 0, contacts);
        //** Attach the adapter to a ListView
        ListView listView = findViewById(R.id.lvContacts);
        listView.setAdapter(adapter);

        //** TODO: Remove this Item listener in the future if no added features will create in Contacts Activity
        //** Set Item listener
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //** Get TextView value using id
                TextView textView_userId = view.findViewById(R.id.textView_userId);
                String userId = textView_userId.getText().toString();
                Toast.makeText(Contacts.this, "ID: " + userId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //** Get checked contact
    public void getCheckedContact() {
        bSendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListView mainListView = findViewById(R.id.lvContacts);
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
                Toast.makeText(Contacts.this, "list "+objects.toString(), Toast.LENGTH_SHORT).show();
                //** Get all Array Object
//                extra.putSerializable("objects", objects); //** Put checked item into session
//                Intent intent = new Intent(getBaseContext(), NearByPlaces.class);
//                intent.putExtra("extra", extra); //** Put all id in putExtra session
//                intent.putExtra("EXTRA_SESSION_ID", SessionId);
                //** Check Array Object length
//                if (objects.size() > 0) { //** If there is Checked contact
//                    startActivity(intent);
//
//                } else {
//                    //** No Contact Checked
//                    Toast.makeText(Contacts.this, "No Contact Selected", Toast.LENGTH_SHORT).show();
//                }


            }
        });
    }


    // Get all user EXTRA sessionId
    public void getListUsers() {
        Bundle extra = getIntent().getBundleExtra("extra");
        //** Put all session extra to ArrayList
        objects = (ArrayList<Object>) extra.getSerializable("objects");
        Toast.makeText(this, "getListUsers "+ objects.toString(), Toast.LENGTH_SHORT).show();

    }

}
