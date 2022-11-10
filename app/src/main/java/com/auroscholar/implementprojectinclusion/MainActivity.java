package com.auroscholar.implementprojectinclusion;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pi.projectinclusion.activity.SplashActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
    }
}