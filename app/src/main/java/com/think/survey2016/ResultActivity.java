package com.think.survey2016;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ResultActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                Intent i = new Intent(ResultActivity.this, FirstActivity.class);
                startActivity(i);

                // close this activity
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
    }
}