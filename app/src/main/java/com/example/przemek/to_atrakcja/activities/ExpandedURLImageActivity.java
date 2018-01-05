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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.przemek.to_atrakcja.R;
import com.example.przemek.to_atrakcja.other.TouchImageView;

import java.io.InputStream;

public class ExpandedURLImageActivity extends Activity
{
    String MapURL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_url_image);
        Intent intent = getIntent();
        MapURL = intent.getStringExtra("MapURL");
        TouchImageView URLImage = (TouchImageView) LayoutInflater.from(this).inflate(R.layout.expanded_url_image, null);
        RelativeLayout Container = (RelativeLayout) findViewById(R.id.ExpandedURLImageContainter);
        Container.addView(URLImage);

        new ExpandedURLImageActivity.DownloadExtendedImageTask(URLImage).execute(MapURL);
    }

    @Override
    public void onBackPressed()
    {
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
    }

    public void Delete (View view)
    {
    }

    public void Download (View view)
    {
    }
}
