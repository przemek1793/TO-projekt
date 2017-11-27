package com.example.przemek.to_atrakcja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void GPS (View view)
    {
        Intent intent = new Intent(this, GPSActivity.class);
        startActivity(intent);
    }

    public void Gallery (View view)
    {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    public void Places (View view)
    {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    public void Options (View view)
    {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    public void Login (View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
    }
}
