package com.think.survey2016;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                String uid = getSharedPreferences("PREF", MODE_PRIVATE).getString("UID", null);

                if (uid==null) {

                    //	Toast.makeText(getApplicationContext(), "in true", Toast.LENGTH_LONG).show();
                    // 1) Launch the authentication activity

                    //	  setContentView(R.layout.welcome_screen);


                    // 2) Then save the state

                    Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();

                } else {
                    // Toast.makeText(getApplicationContext(), "in false", Toast.LENGTH_LONG).show();
                    String uid1 = getSharedPreferences("PREF", MODE_PRIVATE).getString("UID", null);
                    Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                    i.putExtra("UID",uid1);
                    startActivity(i);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }

}