package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.przemek.to_atrakcja.R;

public class AddPlacesActivity extends Activity
{
    private static String URL_add_place = "http://192.168.0.13/add_place.php";

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

    public void AddPlacesData (View view)
    {
        Intent intent = new Intent(this, AddPlacesActivity.class);
        startActivity(intent);
    }

    public void TryToAddPlace (View view)
    {
    }
}
