package com.example.amplify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is logged in, start the main activity
                    startActivity(new Intent(Splash.this, MainActivity.class));
                } else {
                    // User is not logged in, start the sign-up activity
                    startActivity(new Intent(Splash.this, SignUp.class));
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 5000);
    }
}
