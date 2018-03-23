package com.example.henriktre.lab3;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    MainActivityView layout;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new MainActivityView(this);
        setContentView(layout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        layout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.resume();
    }
}
