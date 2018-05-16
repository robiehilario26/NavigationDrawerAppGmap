package com.xperiasola.philubiq64wi.navigationdrawerapp.google_map_activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xperiasola.philubiq64wi.navigationdrawerapp.R;
import com.xperiasola.philubiq64wi.navigationdrawerapp.activities.EditInvitation;
import com.xperiasola.philubiq64wi.navigationdrawerapp.google_map_services.GetNearbyPlacesData;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.EventInfoModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.network.DatabaseHelper;


public class NearByPlaces extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    double latitude;
    double longitude;
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
    private DatabaseHelper myDb;
    private SupportMapFragment mapFragment;
    private BottomSheetDialog mBottomSheetDialog;
    private EventInfoModel eventInfoModel;
    private String finalTitle, finalPlaceName, finalDate,
            finalStart, finalEnd, finalRemarks, finalPlace;
    private int ticketId;
    private Boolean isAdvance;

    boolean internet_connection() {
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_places);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            setContentView(R.layout.activity_near_by_places);
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Array of place types
        mPlaceType = getResources().getStringArray(R.array.place_type);

        // Array of place type names
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

        // Creating an array adapter with an array of Place types
        // to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

        // Getting reference to the Spinner
        mSprPlaceType = findViewById(R.id.spr_place_type_test);

        // Setting adapter on Spinner to set place types
        mSprPlaceType.setAdapter(adapter);

        // Initialize button
        btnFind = findViewById(R.id.btn_find);

        // Initialize database connection
        myDb = new DatabaseHelper(this);


        Intent intent = getIntent();
        eventInfoModel = intent.getParcelableExtra("myDataKey");

        // Set textView value
        finalPlaceName = eventInfoModel.getPlaceName();
        finalPlace = eventInfoModel.getPlace();
        finalTitle = eventInfoModel.getEventTitle();
        finalDate = eventInfoModel.getEventDate();
        finalStart = eventInfoModel.getEventStart();
        finalEnd = eventInfoModel.getEventEnd();
        finalRemarks = eventInfoModel.getEventRemarks();
        isAdvance = eventInfoModel.getAdvance();
        ticketId = eventInfoModel.getTicketId();

    }


    //** Checking if google place services is installed
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
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
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    //** Get current location
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        //** Find nearby places base on the category selected
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
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(NearByPlaces.this);
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(NearByPlaces.this, "Nearby " + mSprPlaceType.getSelectedItem().toString(), Toast.LENGTH_LONG).show();


                } else {

                    //** Create a snackbar telling the user there is no internet connection and issuing a chance to reconnect
                    Snackbar snackbar = Snackbar.make(mapFragment.getView(),
                            "No internet connection.",
                            Snackbar.LENGTH_INDEFINITE);
                    //** Snackbar.make(mapFragment.getView(), "Click the pin for more options", Snackbar.LENGTH_LONG).show();
                    snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorAccent)).show();
                    snackbar.setAction(R.string.try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //** Perform click button find again
                            Toast.makeText(NearByPlaces.this, "Nearby " + mSprPlaceType.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
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

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.setTag(0);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(NearByPlaces.this, "Your Current Location", Toast.LENGTH_LONG).show();

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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
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
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        //Integer clickCount = (Integer) marker.getTag();
        //Toast.makeText(NearByPlaces.this, marker.getTitle(), Toast.LENGTH_SHORT).show();


       /* Integer clickCount = (Integer) marker.getTag();

        if (clickCount == null) {
            clickCount = 0;

            marker.setTag(clickCount);
            final_snippet = marker.getSnippet();
            Toast.makeText(this, marker.getSnippet() + " has been clicked " + clickCount + " times.", Toast.LENGTH_SHORT).show();

        }

        clickCount = clickCount + 1;
        marker.setTag(clickCount);


        Toast.makeText(this, marker.getSnippet() + " has been clicked " + clickCount + " times.", Toast.LENGTH_SHORT).show();*/

        /*if (clickCount != null) {
            clickCount = clickCount + 1;
            Toast.makeText(this, "clickCount "+clickCount, Toast.LENGTH_SHORT).show();
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this, "more click "+final_snippet, Toast.LENGTH_SHORT).show();
        }*/

       /* if(clickCount == 0 || clickCount != null){
            final String snippet = marker.getSnippet();
            final_snippet = snippet;
            Toast.makeText(this, "first click "+final_snippet, Toast.LENGTH_SHORT).show();
        }*/

        // Create BottomSheet dialog

        View sheetView = this.getLayoutInflater().inflate(R.layout.custom_bottom_dialog, null);
        //  sheetView = this.getLayoutInflater().inflate(R.layout.custom_bottom_dialog, null);
        if (sheetView.getParent() == null) {
            mBottomSheetDialog = new BottomSheetDialog(this);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
        } else {
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();
        }
        // Set layout view of dialog


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
                Toast.makeText(NearByPlaces.this, "Directions not yet available", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });
        // Set onClick listener if Save click
        menu_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NearByPlaces.this, EditInvitation.class);

                // Set string value
                finalPlaceName = marker.getTitle();
                finalPlace = marker.getSnippet();
                finalTitle = eventInfoModel.getEventTitle();
                finalDate = eventInfoModel.getEventDate();
                finalStart = eventInfoModel.getEventStart();
                finalEnd = eventInfoModel.getEventEnd();
                finalRemarks = eventInfoModel.getEventRemarks();
                isAdvance = eventInfoModel.getAdvance();
                ticketId = eventInfoModel.getTicketId();

                // EventInfoModel Parcelable class
                EventInfoModel dataToSend = new EventInfoModel(finalTitle, finalDate, finalStart,
                        finalEnd, finalRemarks, finalPlace, isAdvance, finalPlaceName, ticketId);
                intent.putExtra("myDataKey", dataToSend); // Pass value
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent); // Open activity
                mBottomSheetDialog.dismiss();
                finish();
            }
        });

        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

}