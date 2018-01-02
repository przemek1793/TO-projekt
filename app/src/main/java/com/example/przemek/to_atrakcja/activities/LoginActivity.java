package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private static String URL_check_login = "http://192.168.0.13/android_connect/check_login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    class LogIn extends AsyncTask<String, String, String> {

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
                EditText InputPassword=findViewById(R.id.InputPassword);
                EditText InputLogin=findViewById(R.id.InputLogin);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("login", InputLogin.getText().toString());
                jsonObject.put("Password", InputPassword.getText().toString());
                String message = jsonObject.toString();

                url=new URL(URL_check_login);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setFixedLengthStreamingMode(message.getBytes().length);

                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(message.getBytes());

                os.flush();

    //            is = urlConnection.getInputStream();

   //             Button TryToLogButton=(Button)findViewById(R.id.TryToLogButton);

   //             String line;
    //            StringBuilder sb = new StringBuilder();
   //             BufferedReader br = new BufferedReader(new InputStreamReader(is));
   //             while ((line = br.readLine()) != null) {
   //                 sb.append(line);
   //             }
  //              TryToLogButton.setText(sb.toString());

                os.close();
  //              is.close();

                urlConnection.disconnect();

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
        }

    }

    public void TryToLogIn (View view)
    {
        new LogIn().execute();
    }
}
