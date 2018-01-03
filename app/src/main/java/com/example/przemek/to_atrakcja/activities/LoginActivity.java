package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Created by Przemek on 27.11.2017.
 */
public class LoginActivity extends Activity {

    private static String URL_check_login = "http://192.168.0.13/check_login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    class LogIn extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button TryToLogButton=(Button)findViewById(R.id.TryToLogButton);
            TryToLogButton.setText("Logowanie");
        }

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os = null;
            InputStream is = null;
            try {
                String message;
                EditText InputPassword=findViewById(R.id.InputPassword);
                EditText InputLogin=findViewById(R.id.InputLogin);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("login", InputLogin.getText().toString());
                jsonObject.put("Password", InputPassword.getText().toString());
                message = jsonObject.toString();

                url=new URL(URL_check_login);
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

    //            JSONArray Jarray = new JSONArray(response);
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
                if (jsonResponse.getInt("success")==1)
                {
                    SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("Zalogowano?","tak").apply();
                    Intent intent = new Intent(LoginActivity.this, AdministratorActivity.class);
                    startActivity(intent);
                }
                else
                {
                    TextView DatabaseResponse = (TextView) findViewById(R.id.DatabaseResponse);
                    DatabaseResponse.setText(jsonResponse.getString("message"));
                    Button TryToLogButton=(Button)findViewById(R.id.TryToLogButton);
                    TryToLogButton.setText("Zaloguj");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void TryToLogIn (View view)
    {
        new LogIn().execute();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
