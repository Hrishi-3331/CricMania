package com.hrishi_3331.devstudio3331.cricmania;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crick Mania");
    }
}
