package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.przemek.to_atrakcja.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Przemek on 27.11.2017.
 */
public class GPSActivity extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String URL_add_place = "http://192.168.0.13/add_place.php";
    private static String URL_get_places = "http://192.168.0.13/get_places.php";
    private FusedLocationProviderClient FusedLocationClient;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LatLng PlaceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_gps);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new PopulateMapWithMarkers().execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(GPSActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
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

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "brak uprawnien", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap=map;
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                final LatLng punkt=point;
                SharedPreferences sharedPreferences = GPSActivity.this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                String zalogowano = sharedPreferences.getString("Zalogowano?","nie");
                if (zalogowano.equals("tak"))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(GPSActivity.this, R.style.MyDialogTheme);
                    dialog.setTitle("Dodawanie znacznika");
                    dialog.setMessage("Czy chcesz dodać nowy znacznik w miejscu które kliknąłeś");
                    dialog.setNegativeButton("Nie", null);
                    dialog.setPositiveButton("Tak", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            PlaceLocation=punkt;
                            new AddPlace().execute();
                            android.os.SystemClock.sleep(1000);
                            new PopulateMapWithMarkers().execute();
                        }
                    });
                    dialog.show();


                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

    }

    public void NormalMap (View view)
    {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void HybridMap (View view)
    {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void TerrainMap (View view)
    {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    class AddPlace extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;
                double latitude,longitude;

                try
                {
                    longitude = PlaceLocation.longitude;
                    latitude = PlaceLocation.latitude;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    longitude = 0;
                    latitude = 0;
                }

                String Latitude,Longitude;

                if (latitude!=0 || longitude!=0)
                {
                    Latitude = String.valueOf(latitude);
                    Latitude=Latitude.replaceAll("\\.","ǤЖ");
                    Longitude = String.valueOf(longitude);
                    Longitude=Longitude.replaceAll("\\.","ǤЖ");
                }
                else
                {
                    Latitude="brak";
                    Longitude="brak";
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Latitude", Latitude);
                jsonObject.put("Longitude", Longitude);
                message = jsonObject.toString();

                url=new URL(URL_add_place);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setFixedLengthStreamingMode(message.getBytes().length);

                urlConnection.connect();

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(message.getBytes("UTF-8"));

                os.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class PopulateMapWithMarkers extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(URL_get_places);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);

                urlConnection.connect();

                urlConnection.getResponseMessage();
                urlConnection.getResponseCode();

                InputStreamReader aa= new InputStreamReader((urlConnection.getInputStream()));
                BufferedReader br = new BufferedReader(aa);

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                response=sb.toString();
                br.close();

                urlConnection.disconnect();

                jsonResponse = new JSONObject(response) ;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            try
            {
                if (jsonResponse.getInt("success")==1)
                {
                    JSONArray markers=(JSONArray ) jsonResponse.get("Places");
                    for(int n = 0; n < markers.length(); n++)
                    {
                        JSONObject object = markers.getJSONObject(n);
                        double latitude = Double.parseDouble(object.getString("Latitude").replaceAll("ǤЖ","\\."));
                        double longitude = Double.parseDouble(object.getString("Longitude").replaceAll("ǤЖ","\\."));
                        String snippet = object.getString("Hours").replaceAll("ǤЖ","\\.") + "\n" + object.getString("Description").replaceAll("ǤЖ","\\.");
                        String nazwa = object.getString("NAME").replaceAll("ǤЖ","\\.");
                        LatLng pozycja = new LatLng(latitude,longitude);
                        mGoogleMap.addMarker(new MarkerOptions().position(pozycja).title(nazwa).snippet(snippet));
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        private Context context;

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

            LinearLayout info = new LinearLayout(context);
            info.setOrientation(LinearLayout.VERTICAL);

            TextView title = new TextView(context);
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);
            title.setText(marker.getTitle());

            TextView snippet = new TextView(context);
            snippet.setTextColor(Color.GRAY);
            snippet.setText(marker.getSnippet());

            info.addView(title);
            info.addView(snippet);

            SharedPreferences sharedPreferences = GPSActivity.this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
            String zalogowano = sharedPreferences.getString("Zalogowano?","nie");
            if (zalogowano.equals("tak"))
            {
                Button edytuj=new Button(context);
                edytuj.setText("edytuj");
                edytuj.setBackgroundColor(Color.WHITE);

                Button usun=new Button(context);
                usun.setText("usun");
                usun.setBackgroundColor(Color.WHITE);

                info.addView(edytuj);
                info.addView(usun);
            }

            return info;
        }
    }
}
