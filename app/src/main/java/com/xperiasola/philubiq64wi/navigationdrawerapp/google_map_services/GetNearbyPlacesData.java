package com.xperiasola.philubiq64wi.navigationdrawerapp.google_map_services;

/**
 * Created by philUbiq64wi on 2/12/2018.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    private Context mContext;

    public GetNearbyPlacesData() {

    }


    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }


    public GetNearbyPlacesData(final Context context) {
        mContext = context;
    }


    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Toast.makeText(mContext,
                    "Check Internet Connection",
                    Toast.LENGTH_SHORT).show();
        } else {

            Log.d("GooglePlacesReadTask", "onPostExecute Entered");
            List<HashMap<String, String>> nearbyPlacesList;
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(result);
            ShowNearbyPlaces(nearbyPlacesList);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");


        }


    }


    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            // for(HashMap<String, String> googlePlace : nearbyPlacesList)
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");

            LatLng latLng = new LatLng(lat, lng);


            // TODO: Remove this code when final release. This code is about calculating distance

         /*   Location locationA = new Location("point A");
            locationA.setLatitude(latLngA.latitude);
            LatLng latLngA = new LatLng(14.557705, 121.013207);
            LatLng latLngB = new LatLng(lat,lng);

            locationA.setLongitude(latLngA.longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(latLngB.latitude);
            locationB.setLongitude(latLngB.longitude);

            double distance = locationA.distanceTo(locationB);
            System.out.println("place: "+ placeName +"distance: "+distance);*/

            markerOptions.position(latLng);
            markerOptions.snippet(vicinity);
            markerOptions.title(placeName);
            mMap.addMarker(markerOptions);
            mMap.addMarker(markerOptions).setTag(0);


            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

           /* Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(placeName+" : "+vicinity)
                    .snippet("Snippet")
            );

            marker.showInfoWindow();*/

        }


    }
}