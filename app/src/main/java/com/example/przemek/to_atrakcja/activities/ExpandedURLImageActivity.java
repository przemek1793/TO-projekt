package com.example.przemek.to_atrakcja.activities;

import android.app.Activity;
import android.os.Bundle;
import com.example.przemek.to_atrakcja.R;

public class ExpandedURLImageActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_url_image);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
