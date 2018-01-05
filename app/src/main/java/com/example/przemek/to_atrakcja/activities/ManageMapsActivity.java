package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.przemek.to_atrakcja.R;

public class ManageMapsActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managemaps);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public void AddMaps (View view)
    {
        Intent intent = new Intent(this, AddMapsActivity.class);
        startActivity(intent);
    }

    public void DeleteMaps (View view)
    {
        Intent intent = new Intent(this, DeleteMapsActivity.class);
        startActivity(intent);
    }
}
