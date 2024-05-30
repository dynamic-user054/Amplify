package com.example.amplify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView btnMainFrag, btnProfileFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MainRecycler())
                    .commit();
        }

        btnMainFrag = findViewById(R.id.BtnMainFrag);
        btnProfileFrag = findViewById(R.id.BtnProfileFrag);

        btnMainFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new MainRecycler())
                        .addToBackStack(null)
                        .commit();

            }
        });

        btnProfileFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new Profile())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
}
