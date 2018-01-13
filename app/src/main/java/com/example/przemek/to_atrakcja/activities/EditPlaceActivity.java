package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.przemek.to_atrakcja.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditPlaceActivity extends Activity
{
    String nazwa;
    String latitude,longitude;
    private static String URL_get_place = "http://192.168.0.13/get_place.php";
    private static String URL_delete_place = "http://192.168.0.13/delete_place.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        Intent intent = getIntent();
        String pozycja = intent.getStringExtra("pozycja");
        String[] latlong =  pozycja.split(",");
        latitude = latlong[0];
        latitude=latitude.substring(10);
        longitude = latlong[1];
        longitude=longitude.substring(0,longitude.length() - 1);
        nazwa = intent.getStringExtra("nazwa");
        new GetPlace().execute();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, GPSActivity.class);
        startActivity(intent);
        finish();
    }

    public void TryToEditPlace (View view)
    {
    }

    public void TryToDeletePlace (View view)
    {
        new DeletePlace().execute();
    }

    class GetPlace extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;

                JSONObject jsonObject = new JSONObject();

                String latitude1=latitude.replaceAll("\\.","ǤЖ");
                String longitude1=longitude.replaceAll("\\.","ǤЖ");

                jsonObject.put("Latitude", latitude1);
                jsonObject.put("Longitude", longitude1);
                message = jsonObject.toString();

                url=new URL(URL_get_place);
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
                    JSONObject object = markers.getJSONObject(0);
                    String latitude = object.getString("Latitude").replaceAll("ǤЖ","\\.");
                    String longitude = object.getString("Longitude").replaceAll("ǤЖ","\\.");
                    String hours = object.getString("Hours").replaceAll("ǤЖ","\\.");
                    String Opis=object.getString("Description").replaceAll("ǤЖ","\\.");
                    String Typ=object.getString("Type").replaceAll("ǤЖ","\\.");
                    String nazwa = object.getString("Name").replaceAll("ǤЖ","\\.");

                    String[] godziny =  hours.split("-");
                    String openingHours=godziny[0];
                    String closingHours;
                    if (godziny.length>1)
                    {
                        closingHours=godziny[1];
                    }
                    else
                    {
                        closingHours="";
                    }

                    EditText InputPlaceNameEdit = (EditText) findViewById(R.id.InputPlaceNameEdit);
                    InputPlaceNameEdit.setText(nazwa);
                    EditText InputPlaceTypeEdit = (EditText) findViewById(R.id.InputPlaceTypeEdit);
                    InputPlaceTypeEdit.setText(Typ);
                    EditText InputPlaceOpeningHourEdit = (EditText) findViewById(R.id.InputPlaceOpeningHourEdit);
                    InputPlaceOpeningHourEdit.setText(openingHours);
                    EditText InputPlaceClosingHoursEdit = (EditText) findViewById(R.id.InputPlaceClosingHoursEdit);
                    InputPlaceClosingHoursEdit.setText(closingHours);
                    EditText InputPlaceDescriptionEdit = (EditText) findViewById(R.id.InputPlaceDescriptionEdit);
                    InputPlaceDescriptionEdit.setText(Opis);
                    EditText InputPLaceLongitudeEdit = (EditText) findViewById(R.id.InputPLaceLongitudeEdit);
                    InputPLaceLongitudeEdit.setText(longitude);
                    EditText InputPlaceLatitudeEdit = (EditText) findViewById(R.id.InputPlaceLatitudeEdit);
                    InputPlaceLatitudeEdit.setText(latitude);
                }
                else
                {
                    String Typ="adasd";
                    EditText InputPlaceTypeEdit = (EditText) findViewById(R.id.InputPlaceTypeEdit);
                    InputPlaceTypeEdit.setText(Typ);
                    EditText InputPLaceLongitudeEdit = (EditText) findViewById(R.id.InputPLaceLongitudeEdit);
                    InputPLaceLongitudeEdit.setText(longitude);
                    EditText InputPlaceLatitudeEdit = (EditText) findViewById(R.id.InputPlaceLatitudeEdit);
                    InputPlaceLatitudeEdit.setText(latitude);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    class DeletePlace extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;

                JSONObject jsonObject = new JSONObject();

                String latitude1=latitude.replaceAll("\\.","ǤЖ");
                String longitude1=longitude.replaceAll("\\.","ǤЖ");

                jsonObject.put("Latitude", latitude1);
                jsonObject.put("Longitude", longitude1);
                message = jsonObject.toString();

                url=new URL(URL_delete_place);
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

        protected void onPostExecute(String file_url)
            {
                try
                {
                    if (jsonResponse.getInt("success")==1)
                    {
                        TextView DatabaseResponse = (TextView) findViewById(R.id.DatabaseResponseEditPlace);
                        DatabaseResponse.setText(jsonResponse.getString("message"));
                    }
                    else
                    {
                        TextView DatabaseResponse = (TextView) findViewById(R.id.DatabaseResponseEditPlace);
                        DatabaseResponse.setText(jsonResponse.getString("message"));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
        }

    }
}
