package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.example.przemek.to_atrakcja.R;

public class EditPlaceActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public void TryToEditPlace (View view)
    {
    }

    public void TryToDeletePlace (View view)
    {
    }
}
