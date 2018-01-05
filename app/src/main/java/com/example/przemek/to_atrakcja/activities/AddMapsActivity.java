package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
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

public class AddMapsActivity extends Activity
{
    private static String URL_add_map = "http://192.168.0.13/add_map.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmaps);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, AdministratorActivity.class);
        startActivity(intent);
        finish();
    }

    public void TryToAddMap (View view)
    {
        new AddMap().execute();
    }

    class AddMap extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button TryToLogButton=(Button)findViewById(R.id.TryToAddMapButton);
            TryToLogButton.setText("Dodawanie mapy");
        }

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;
                EditText InputMapName=findViewById(R.id.InputMapName);
                EditText InputMapURL=findViewById(R.id.InputMapURL);
                String MapURL=InputMapURL.getText().toString();
                MapURL=MapURL.replaceAll("\\.","ǤЖ");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Name", InputMapName.getText().toString());
                jsonObject.put("MapURL", MapURL);
                message = jsonObject.toString();

                url=new URL(URL_add_map);
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
                TextView DatabaseResponse = (TextView) findViewById(R.id.DatabaseResponseAddMap);
                DatabaseResponse.setText(jsonResponse.getString("message"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            Button TryToLogButton=(Button)findViewById(R.id.TryToAddMapButton);
            TryToLogButton.setText("Dodaj mapę");
        }

    }
}
