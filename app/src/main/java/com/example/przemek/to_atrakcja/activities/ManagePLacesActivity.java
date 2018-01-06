package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
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
    }

    public void AddPLacesCurrent (View view)
    {
    }

    public void AddPLacesMap (View view)
    {
    }

    public void ManagePlacesAdmin (View view)
    {
    }

}
