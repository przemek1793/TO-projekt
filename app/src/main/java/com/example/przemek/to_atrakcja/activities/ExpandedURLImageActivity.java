package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.przemek.to_atrakcja.R;
import com.example.przemek.to_atrakcja.other.TouchImageView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExpandedURLImageActivity extends Activity
{
    private static String URL_delete_map = "http://192.168.0.13/delete_map.php";
    private static String URL_download_map = "http://192.168.0.13/download_map";

    String cel, MapURL;
    TouchImageView URLImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_url_image);
        Intent intent = getIntent();
        String CzyPoprawny;
        MapURL = intent.getStringExtra("MapURL");
        CzyPoprawny = intent.getStringExtra("CzyPoprawny");
        URLImage = (TouchImageView) LayoutInflater.from(this).inflate(R.layout.expanded_url_image, null);
        RelativeLayout Container = (RelativeLayout) findViewById(R.id.ExpandedURLImageContainter);
        Container.addView(URLImage);
        cel = intent.getStringExtra("Cel");
        Button przycisk = (Button) findViewById(R.id.ExpandedURLIMageButton);
        if (cel.equals("Usuwanie"))
        {
            przycisk.setText("Usuń");
        }
        else
        {
            przycisk.setText("Ściągnij");
        }

        if (CzyPoprawny.equals("nie"))
        {
            URLImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.error_obraz));
        }
        else
        {
            new ExpandedURLImageActivity.DownloadExtendedImageTask(URLImage).execute(MapURL);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(ExpandedURLImageActivity.this, DeleteMapsActivity.class);
        startActivity(intent);
        finish();
    }

    private class DownloadExtendedImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadExtendedImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
        }
    }

    public void ExpandedURLImageButtonAction (View view)
    {
        if (cel.equals("Usuwanie"))
        {
            new DeleteMap().execute();
        }
        else
        {
            Download(view);
        }
    }

    public void Download (View view)
    {
    }

    class DeleteMap extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Button TryToLogButton=(Button)findViewById(R.id.ExpandedURLIMageButton);
            TryToLogButton.setText("Usuwanie");
        }

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            OutputStream os;
            try {
                String message;

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MapURL", MapURL.replaceAll("\\.","ǤЖ"));
                message = jsonObject.toString();

                url=new URL(URL_delete_map);
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
                if (jsonResponse.getInt("success")==1)
                {
                    Intent intent = new Intent(ExpandedURLImageActivity.this, DeleteMapsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Button TryToLogButton=(Button)findViewById(R.id.ExpandedURLIMageButton);
                    TryToLogButton.setText(jsonResponse.getString("message"));
                    //            TryToLogButton.setText("Bląd przy usuwaniu mapy");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }
}
