package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.example.przemek.to_atrakcja.R;

public class AdministratorActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void Logout (View view)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Zalogowano?","nie").apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
