package com.sorasync.sorasync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000;
    //variables
    Animation topAnimation, bottomAnimation;
    CircleImageView imageView;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation_bottom);

        imageView = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);

        imageView.setAnimation(topAnimation);
        logo.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}