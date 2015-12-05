package com.jonduran.circularrevealdropdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircularRevealDropdown dropdown = (CircularRevealDropdown) findViewById(R.id.dropdown);
        dropdown.setItems(new String[] {"all", "video", "audio", "photo", "note"});
    }
}
