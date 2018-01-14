package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.przemek.to_atrakcja.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddPlacesActivity extends Activity
{
    private static String URL_add_place = "http://192.168.1.167/add_place.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public void TryToAddPlace (View view)
    {
        new AddPlace().execute();
    }

    class AddPlace extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button TryToAddPlaceButton= findViewById(R.id.TryToAddPlaceButton);
            TryToAddPlaceButton.setText("Dodawanie miejsca");
        }

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;
                EditText InputPlaceName=findViewById(R.id.InputPlaceName);
                EditText InputPlaceOpeningHour=findViewById(R.id.InputPlaceOpeningHour);
                EditText InputPlaceClosingHours=findViewById(R.id.InputPlaceClosingHours);
                EditText InputPlaceDescription=findViewById(R.id.InputPlaceDescription);
                EditText InputPLaceLongitude=findViewById(R.id.InputPLaceLongitude);
                EditText InputPlaceLatitude=findViewById(R.id.InputPlaceLatitude);

                String Description=InputPlaceDescription.getText().toString();
                Description=Description.replaceAll("\\.","ǤЖ");

                String hours=InputPlaceOpeningHour.getText().toString()+ "-"  + InputPlaceClosingHours.getText().toString();
                hours=hours.replaceAll("\\.","ǤЖ");

                String Latitude = InputPlaceLatitude.getText().toString();
                Latitude=Latitude.replaceAll("\\.","ǤЖ");
                String Longitude = InputPLaceLongitude.getText().toString();
                Longitude=Longitude.replaceAll("\\.","ǤЖ");

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
                TextView DatabaseResponse = findViewById(R.id.DatabaseResponseAddPlace);
                DatabaseResponse.setText(jsonResponse.getString("message"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            Button TryToAddPlaceButton= findViewById(R.id.TryToAddPlaceButton);
            TryToAddPlaceButton.setText("Dodaj miejsce");
        }

    }
}
