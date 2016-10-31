package com.dsatija.apps.twittwit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends Activity {
    ImageView ivLogo2;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ivLogo2 = (ImageView) findViewById(R.id.ivLogo2);
        startRotatingImage();
/** set time to splash out */
        final int welcomeScreenDisplay = 3000;
/** create a thread to show splash up to splash time */
        Thread welcomeThread = new Thread() {
            int wait = 0;

            @Override
            public void run() {
                try {
                    super.run();
/**
 * use while to get the splash time. Use sleep() to increase
 * the wait variable for every 100L.
 */
                    while (wait < welcomeScreenDisplay) {
                        sleep(100);
                        wait += 100;


                    }
                } catch (Exception e) {
                    System.out.println("EXc=" + e);
                } finally {
/**
 * Called after splash times up. Do some action after splash
 * times up. Here we moved to another main activity class
 */
                    startActivity(new Intent(SplashScreenActivity.this,
                            LoginActivity.class));
                    finish();
                }
            }
        };
        welcomeThread.start();


    }

    public void startRotatingImage() {
        Animation startRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        ivLogo2.startAnimation(startRotateAnimation);

    }


}