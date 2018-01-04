package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.przemek.to_atrakcja.R;

public class AddMaps extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmaps);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, AdministratorActivity.class);
        startActivity(intent);
    }

    public void TryToAddMap (View view)
    {
  //      new LogIn().execute();
    }
}
