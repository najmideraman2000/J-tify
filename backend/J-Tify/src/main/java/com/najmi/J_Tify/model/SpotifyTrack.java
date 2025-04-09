package com.najmi.j_tify.model;

import lombok.Data;

@Data
public class SpotifyTrack {
    private String id;
    private String name;
    private String artist;
    private String albumImageUrl;

    public SpotifyTrack(String id, String name, String artist, String albumImageUrl) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.albumImageUrl = albumImageUrl;
    }
}