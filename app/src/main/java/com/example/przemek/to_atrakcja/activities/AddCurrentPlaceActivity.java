package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.przemek.to_atrakcja.R;
import com.google.android.gms.location.LocationListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCurrentPlaceActivity extends Activity implements LocationListener
{
    Location location1;
    private static String URL_add_place = "http://192.168.1.167/add_place.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_current_place);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public void TryToAddCurrentPlace (View view)
    {
        new AddPlace().execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            location1 =location ; }
    }

    class AddPlace extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button TryToAddPlaceButton= findViewById(R.id.TryToAddCurrentPlaceButton);
            TryToAddPlaceButton.setText("Dodawanie miejsca");
        }

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;
                EditText InputPlaceName=findViewById(R.id.InputCurrentPlaceName);
                EditText InputPlaceOpeningHour=findViewById(R.id.InputCurrentPlaceOpeningHour);
                EditText InputPlaceClosingHours=findViewById(R.id.InputCurrentPlaceClosingHours);
                EditText InputPlaceDescription=findViewById(R.id.InputCurrentPlaceDescription);
                double latitude,longitude;


                try
                {
                    longitude = location1.getLongitude();
                    latitude = location1.getLatitude();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    longitude = 0;
                    latitude = 0;
                }


                String Description=InputPlaceDescription.getText().toString();
                Description=Description.replaceAll("\\.","ǤЖ");

                String hours=InputPlaceOpeningHour.getText().toString()+ "-"  + InputPlaceClosingHours.getText().toString();
                hours=hours.replaceAll("\\.","ǤЖ");

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
                jsonObject.put("Name", InputPlaceName.getText().toString());
                jsonObject.put("Hours", hours);
                jsonObject.put("Description", Description);
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
                os.close();
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
            try {
                TextView DatabaseResponse = findViewById(R.id.DatabaseResponseAddPlaceCurrent);
                DatabaseResponse.setText(jsonResponse.getString("message"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            Button TryToAddPlaceButton= findViewById(R.id.TryToAddCurrentPlaceButton);
            TryToAddPlaceButton.setText("Dodaj miejsce");
        }

    }
}
