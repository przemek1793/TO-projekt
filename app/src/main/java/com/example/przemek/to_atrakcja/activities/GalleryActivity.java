package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.przemek.to_atrakcja.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Przemek on 27.11.2017.
 */
public class GalleryActivity extends Activity
{
    private static String URL_get_maps = "http://192.168.0.13/get_maps.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        new PopulateTable().execute();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    class PopulateTable extends AsyncTask<String, String, String> {

        JSONObject jsonResponse;
        String response;

        protected String doInBackground(String... args) {

            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(URL_get_maps);
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
                    TableLayout table = findViewById(R.id.TableGallery);
                    JSONArray jsonrzedy=(JSONArray ) jsonResponse.get("Mapy");
                    for(int n = 0; n < jsonrzedy.length(); n++)
                    {
                        JSONObject object = jsonrzedy.getJSONObject(n);
                        TableRow row = (TableRow) LayoutInflater.from(GalleryActivity.this).inflate(R.layout.map_table_row, null);
                        TextView TextName = (TextView) LayoutInflater.from(GalleryActivity.this).inflate(R.layout.table_name_cell, null);
                        final ImageView URLImage = (ImageView) LayoutInflater.from(GalleryActivity.this).inflate(R.layout.table_url_cell, null);

                        final String MapURL = object.getString("MAPURL").replaceAll("ǤЖ","\\.");
                        final String Name =object.getString("NAME");
                        TextName.setText(Name);

                        URLImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ZoomedIMage(URLImage,MapURL,Name);
                            }
                        });

                        new GalleryActivity.DownloadImageTask(URLImage).execute(MapURL);

                        table.addView(row);
                        row.addView(TextName);
                        row.addView(URLImage);
                    }
                }
                else
                {
                    try
                    {
                        TextView DatabaseResponse = findViewById(R.id.DatabaseResponseGallery);
                        DatabaseResponse.setText(jsonResponse.getString("message"));
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                if (mIcon11==null)
                {
                    mIcon11=BitmapFactory.decodeResource(GalleryActivity.this.getResources(), R.drawable.error_obraz);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();

                mIcon11=BitmapFactory.decodeResource(GalleryActivity.this.getResources(), R.drawable.error_obraz);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
        }
    }

    private void ZoomedIMage (ImageView URLImage, String MapURL, String Name)
    {
        Bitmap bitmap = ((BitmapDrawable)URLImage.getDrawable()).getBitmap();
        try
        {
            if (!bitmap.sameAs(BitmapFactory.decodeResource(GalleryActivity.this.getResources(), R.drawable.error_obraz)))
            {
                Intent intent = new Intent(this, ExpandedURLImageActivity.class);
                intent.putExtra("MapURL", MapURL);
                intent.putExtra("CzyPoprawny", "tak");
                intent.putExtra("Cel", "Ściągnij");
                intent.putExtra("Name", Name);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(this, ExpandedURLImageActivity.class);
                intent.putExtra("MapURL", MapURL);
                intent.putExtra("CzyPoprawny", "nie");
                intent.putExtra("Cel", "Ściągnij");
                intent.putExtra("Name", Name);
                startActivity(intent);
                finish();
            }
        }
        catch (NullPointerException e)
        {
            Intent intent = new Intent(this, ExpandedURLImageActivity.class);
            intent.putExtra("MapURL", MapURL);
            intent.putExtra("CzyPoprawny", "nie");
            intent.putExtra("Cel", "Ściągnij");
            intent.putExtra("Name", Name);
            startActivity(intent);
        }
    }
}
