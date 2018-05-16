package com.xperiasola.philubiq64wi.navigationdrawerapp.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.activities.CustomNavigationDrawer;
import com.xperiasola.philubiq64wi.navigationdrawerapp.google_map_services.GetNearbyPlacesData;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;
import com.xperiasola.philubiq64wi.navigationdrawerapp.utility.customButtomDialog.BottomDialogEventInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.getDefault;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    //** Set Radius search of area
    private int PROXIMITY_RADIUS = 1000;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private Spinner mSprPlaceType;
    private String[] mPlaceType = null;
    private String[] mPlaceTypeName = null;
    private Button btnFind;
    private ArrayList<Object> objects;
    private String latestTicketId;
    private DatabaseHelper myDb;
    private SupportMapFragment mapFragment;
    private BottomSheetDialog mBottomSheetDialog;
    private StringBuffer bufferCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    Geocoder geocoder;
    List<Address> addresses;
    private View rootView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private BottomDialogEventInfo bottomDialogEventInfo;


    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        // Set spinner resource value
        mPlaceType = getResources().getStringArray(R.array.place_type);

        // Array of place type names
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

        // Creating an array adapter with an array of Place types
        // to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

        // Getting reference to the Spinner
        mSprPlaceType = rootView.findViewById(R.id.spr_place_type_test);

        // Setting adapter on Spinner to set place types
        mSprPlaceType.setAdapter(adapter);

        // Initialize button
        btnFind = rootView.findViewById(R.id.btn_find);


        // Disabled button find until map is not ready
        btnFind.setEnabled(false);

        // Initialize database connection
        myDb = new DatabaseHelper(getActivity());



//        /** Alternative way to find current location:
//         * Android LocationServices.FusedLocationApi deprecated
//         * Please continue using the FusedLocationProviderApi class and don't migrate to
//         * the FusedLocationProviderClient class until Google Play services version 12.0.0 is available,
//         * which is expected to ship in early 2018. Using the FusedLocationProviderClient before version 12.0.0
//         * causes the client app to crash when Google Play services is updated on the device. We apologize for
//         * any inconvenience this may have caused.
//         *
//         * TODO: See support documents here:
//         * https://stackoverflow.com/questions/46481789/android-locationservices-fusedlocationapi-deprecated
//         **/
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        return rootView;
    }


    // Set action bar title into "Map"
    public void onResume() {
        super.onResume();
        // Set title bar
        ((CustomNavigationDrawer) getActivity()).getSupportActionBar().setTitle("Map");

    }

    // Checking internet connection first

    boolean internet_connection() {
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }



    //** Get all user EXTRA sessionId
    public void getListUsers() {
//        Bundle extra = getActivity().getIntent().getBundleExtra("extra");
//        //** Put all session extra to ArrayList
        objects = (ArrayList<Object>) getArguments().getSerializable("objects");
        bottomDialogEventInfo = new BottomDialogEventInfo(MapViewFragment.this.getContext());
        bottomDialogEventInfo.getUserList(objects);

    }

    //** Insert all contact that invited into table invitation
    public void getSelectContact() {

        //** Get last ticket_id
        Cursor cursorResult = myDb.getLastInvitaionTicket();
        while (cursorResult.moveToNext()) {
            latestTicketId = cursorResult.getString(0); //* Get Column Id value in table invitation_ticket
        }

        //** Insert all contact selected
        for (int x = 0; x < objects.size(); x++) {
            boolean isInserted = myDb.insertInvitationData((String) objects.get(x), latestTicketId);
            if (isInserted == true) {
                //** Inserted Successfully
            } else {
                //** Inserted Failed
                Toast.makeText(getActivity(), "Action: Failed!", Toast.LENGTH_SHORT).show();
            }

        }

        //** Create intent for Invitation
        Intent openInvitationLayout = new Intent(getActivity(), CustomNavigationDrawer.class);
        //** Clear activity before opening new intent
        openInvitationLayout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openInvitationLayout);


    }

    //** Checking if google place services is installed
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    //** Get current location
    public void onMapReady(GoogleMap googleMap) {

        // Enabled button find when map is ready
        btnFind.setEnabled(true);

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        // Find nearby places base on the category selected
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("onClick", "Find Button is Clicked");

                //** Check internet connection
                if (internet_connection()) {
                    // Execute DownloadJSON AsyncTask
                    int selectedPosition = mSprPlaceType.getSelectedItemPosition();
                    String type = mPlaceType[selectedPosition];

                    mMap.clear();
                    if (mCurrLocationMarker != null) {
                        // mCurrLocationMarker.remove();
                    }
                    String url = getUrl(latitude, longitude, type.toString());
                    Object[] DataTransfer = new Object[2];
                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(getActivity());
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(getActivity(), "Nearby " + mSprPlaceType.getSelectedItem().toString(), Toast.LENGTH_LONG).show();


                } else {

                    //** Create a snackbar telling the user there is no internet connection and issuing a chance to reconnect
                    Snackbar snackbar = Snackbar.make(rootView,
                            "No internet connection.",
                            Snackbar.LENGTH_INDEFINITE);
                    //** Snackbar.make(mapFragment.getView(), "Click the pin for more options", Snackbar.LENGTH_LONG).show();
                    snackbar.setActionTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),
                            R.color.colorAccent)).show();
                    snackbar.setAction(R.string.try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //** Perform click button find again
                            Toast.makeText(getActivity(), "Nearby " + mSprPlaceType.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                            btnFind.performClick();

                        }
                    });
                }


            }
        });

        //** TODO: to open new activity on click on marker uncomment this

        /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(NearByPlaces.this,Invitation.class);
                startActivity(intent);
            }
        });*/

        getListUsers();
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapViewFragment.this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Deprecated
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        //** Original API key
        //googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        //** Own API
        googlePlacesUrl.append("&key=" + "AIzaSyCPeSYrznfOmbP5cU7vWXSjkyew012n83E");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            // mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();


        // Set GeoCoder
        geocoder = new Geocoder(MapViewFragment.this.getContext(), getDefault());
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            bufferCurrentLocation = new StringBuffer();
            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                bufferCurrentLocation.append(addresses.get(0).getAddressLine(i)).append(" ");
            }

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions(); // Set new marker
            markerOptions.position(latLng); // Longtitude and Latitude
            markerOptions.title(addresses.get(0).getLocality()); // Local name
            markerOptions.snippet(bufferCurrentLocation.toString()); // Full address
            // Set marker color
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mCurrLocationMarker.setTag(0); // Set tag

            //move map camera
           /* mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));*/


        } catch (IOException e) {
            e.printStackTrace();
        }


        //move map camera
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(11)
                .build();

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        Toast.makeText(getActivity(), "Your Current Location", Toast.LENGTH_LONG).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Create BottomSheet dialog
        View sheetView = this.getLayoutInflater().inflate(R.layout.custom_bottom_dialog, null);
        //  sheetView = this.getLayoutInflater().inflate(R.layout.custom_bottom_dialog, null);
        if (sheetView.getParent() == null) {
            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
        } else {
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
        }
        // Set layout view of dialog


        System.out.println("Marker: " + marker.getTitle() + " " + marker.getSnippet());

        // Find components inside view using Id
        TextView textView_place = sheetView.findViewById(R.id.textViewActivityName);
        TextView textView_vicinity = sheetView.findViewById(R.id.textView_vicinity);
        LinearLayout menu_directions = sheetView.findViewById(R.id.fragment_bottom_sheet_directions);
        LinearLayout menu_save = sheetView.findViewById(R.id.fragment_bottom_sheet_save);

        // Set TextView value
        textView_place.setText(marker.getTitle()); // Name of place
        textView_vicinity.setText(marker.getSnippet());// Vicinity
        // marker.setSnippet(null);


        if (marker != null) {
            marker.remove();
        }

        mMap.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .snippet(
                        marker.getSnippet())
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(marker.getTitle()));


        // Set onClick listener if Direction click
        menu_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Directions not yet available", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        // Set onClick listener if Save click
        menu_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Code created date: 3/5/2018
                /**
                 * This code is for bottom dialog box which is in separate class
                 * its show custom bottom dialog box for Event information
                 * Class automatically retrieve Google map info such as:
                 * 1.location name
                 * 2. full address
                 * 3. event information (Date, Time start, Time end, Remarks
                 * */
                // Dismiss bottom dialog
                mBottomSheetDialog.dismiss();
                // Call Separate Class for custom bottom dialog
                bottomDialogEventInfo = new BottomDialogEventInfo(MapViewFragment.this.getContext());
                bottomDialogEventInfo.dateTimeListenerView();
                bottomDialogEventInfo.getActivityContext(getActivity());
                bottomDialogEventInfo.fetchMarkerInfo(marker.getTitle(), marker.getSnippet());

            }
        });

        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }


}
