package com.example.amplify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Player extends AppCompatActivity {

    ImageView playBtn;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    Handler handler;
    int currentPosition = 0;
    String songResource;
    TextView songNameTextView;
    ImageView songImageView;
    boolean isSeekBarTracking = false;
    ImageView likeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        likeBtn = findViewById(R.id.BtnLike);
        playBtn = findViewById(R.id.BtnPlay);
        seekBar = findViewById(R.id.seekBar);
        songNameTextView = findViewById(R.id.SongName);
        songImageView = findViewById(R.id.imageViewBackground);
        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            songResource = extras.getString("songResourceId");
            String songName = extras.getString("songName");
            String songImageResourceId = extras.getString("songImage");
            songNameTextView.setText(songName);
            songImageView.setImageResource(R.drawable.song_img_placeholder);
            Picasso.get().load(songImageResourceId).into(songImageView);
        }
        checkIfLiked();
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playBtn.setImageResource(R.drawable.play);
                } else {
                    PlaySong();
                    playBtn.setImageResource(R.drawable.pause);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = true;
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = false;
                updateSeekBar();
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like();
            }
        });
    }

    public void PlaySong() {
        if (songResource != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                }

                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(songResource));
                mediaPlayer.prepare();

                if (currentPosition > 0) {
                    mediaPlayer.seekTo(currentPosition);
                }
                mediaPlayer.start();

                updateSeekBar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSeekBar() {
        if (!isSeekBarTracking) {
            seekBar.setMax(mediaPlayer.getDuration());

            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int currPos = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currPos);
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private void Like() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Liked");

            userRef.orderByChild("songName").equalTo(songNameTextView.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // The song is already liked, remove it
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            likeBtn.setImageResource(R.drawable.like);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Player.this, "Failed to unlike song!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {

                                String likeId = userRef.push().getKey();
                                Map<String, Object> songDetails = new HashMap<>();
                                songDetails.put("songName", songNameTextView.getText().toString());
                                songDetails.put("songUrl", songResource);

                                if (likeId != null) {
                                    userRef.child(songNameTextView.getText().toString()).setValue(songDetails)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    likeBtn.setImageResource(R.drawable.liked);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to like song
                                                    Toast.makeText(Player.this, "Failed to like song!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error handling for database operation cancellation
                            Log.e("Like", "Database operation cancelled: " + databaseError.getMessage());
                            Toast.makeText(Player.this, "Database operation cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void checkIfLiked() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Liked");


            if (userRef != null) {
            userRef.orderByChild("songName").equalTo(songNameTextView.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                likeBtn.setImageResource(R.drawable.liked);
                            } else {
                                likeBtn.setImageResource(R.drawable.like);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error handling for database operation cancellation
                            Log.e("CheckIfLiked", "Database operation cancelled: " + databaseError.getMessage());
                            Toast.makeText(Player.this, "Database operation cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }}
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);
    }
}