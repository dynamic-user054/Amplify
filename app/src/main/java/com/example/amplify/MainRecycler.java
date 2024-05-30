package com.example.amplify;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainRecycler extends Fragment implements SelectListener {

    private ArrayList<MusicModel> arrMusic = new ArrayList<>();
    ProgressBar progressBar;

    public MainRecycler() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_recycler, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference songsRef = storageRef.child("Song");
        StorageReference songImagesRef = storageRef.child("SongImg");
        songsRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                String songName = item.getName().replace(".mp3", "");
                item.getDownloadUrl().addOnSuccessListener(songUri -> {
                    songImagesRef.child(songName + ".jpeg").getDownloadUrl().addOnSuccessListener(imageUri -> {
                        arrMusic.add(new MusicModel(songName,songUri.toString(), imageUri.toString()));
                        if (arrMusic.size() == listResult.getItems().size()) {
                            updateAdapter(recyclerView);
                            progressBar.setVisibility(View.GONE);

                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to retrieve song images", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to retrieve songs", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to retrieve songs", Toast.LENGTH_SHORT).show();
        });


        return rootView;
    }

    private void updateAdapter(RecyclerView recyclerView) {
        RecyclerMusicAdapter adapter = new RecyclerMusicAdapter(getActivity(), arrMusic, this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onItemClicked(MusicModel musicModel) {

        Intent toPlayer = new Intent(getActivity(), Player.class);
        Bundle bundle = new Bundle();
        bundle.putString("songResourceId", musicModel.getSongUrl());
        bundle.putString("songName", musicModel.getSongName());
        bundle.putString("songImage", musicModel.getImageUrl());
        toPlayer.putExtras(bundle);
        startActivity(toPlayer);
    }

}