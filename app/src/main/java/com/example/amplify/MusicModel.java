package com.example.amplify;

public class MusicModel {
    private String songName;
    private String songUrl;
    private String imageUrl;
    public MusicModel(String songName, String songUrl, String imageUrl) {
        this.songName = songName;
        this.songUrl = songUrl;
        this.imageUrl = imageUrl;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
