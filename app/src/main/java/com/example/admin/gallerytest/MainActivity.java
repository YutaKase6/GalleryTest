package com.example.admin.gallerytest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * メインのActivity
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new MainActivityFragment()).commit();
        }
    }
}
