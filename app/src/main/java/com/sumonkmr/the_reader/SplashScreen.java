package com.sumonkmr.the_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView introLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        introLogo = findViewById(R.id.introLogo);

        Animation slideIN = AnimationUtils.loadAnimation(this,R.anim.left_to_right);
                        slideIN.setDuration(800);
        introLogo.startAnimation(slideIN);


        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        };

        handler.postDelayed(runnable,900);
    }
}