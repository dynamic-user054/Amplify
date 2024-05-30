package com.example.amplify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class LikedSongs extends AppCompatActivity implements SelectListener {


    private ArrayList<MusicModel> arrMusic = new ArrayList<>();
    ProgressBar progressBar;
    private DatabaseReference likedRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_songs);

        RecyclerView recyclerView = findViewById(R.id.likedRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBarLiked);
        progressBar.setVisibility(View.VISIBLE);
        TextView noLikedSongsTextView = findViewById(R.id.noLikedSongsTextView);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            likedRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Liked");
            StorageReference songImagesRef = FirebaseStorage.getInstance().getReference().child("SongImg");
            likedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    arrMusic.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String songName = snapshot.getKey();
                        HashMap<String, Object> songData = (HashMap<String, Object>) snapshot.getValue();
                        String songUrl = (String) songData.get("songUrl");
                        songImagesRef.child(songName + ".jpeg").getDownloadUrl().addOnSuccessListener(imageUri -> {
                            arrMusic.add(new MusicModel(songName,songUrl.toString(), imageUri.toString()));
                            if (arrMusic.size() == dataSnapshot.getChildrenCount()) {
                                noLikedSongsTextView.setVisibility(View.GONE);
                                updateAdapter(recyclerView);
                                progressBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(LikedSongs.this, "Failed to retrieve song images", Toast.LENGTH_SHORT).show();
                        });
                    }
                    if (arrMusic.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        noLikedSongsTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LikedSongs.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateAdapter(RecyclerView recyclerView) {
        RecyclerMusicAdapter adapter = new RecyclerMusicAdapter(LikedSongs.this, arrMusic, this);
        recyclerView.setAdapter(adapter);
    }

    public void onItemClicked(MusicModel musicModel) {
        Intent toPlayer = new Intent(LikedSongs.this, Player.class);
        Bundle bundle = new Bundle();
        bundle.putString("songResourceId", musicModel.getSongUrl());
        bundle.putString("songName", musicModel.getSongName());
        bundle.putString("songImage", musicModel.getImageUrl());
        toPlayer.putExtras(bundle);
        startActivity(toPlayer);
    }
}
