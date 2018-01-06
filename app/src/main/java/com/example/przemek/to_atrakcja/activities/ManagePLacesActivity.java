package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.przemek.to_atrakcja.R;

public class ManagePLacesActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_places);
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

    public void AddPLacesCurrent (View view)
    {
        Intent intent = new Intent(this, AddCurrentPlaceActivity.class);
        startActivity(intent);
    }


    public void ManagePlacesAdmin (View view)
    {
    }

}
