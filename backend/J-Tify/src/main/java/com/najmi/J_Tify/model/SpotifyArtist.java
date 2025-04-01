package com.najmi.j_tify.model;

import lombok.Data;

@Data
public class SpotifyArtist {
    private String id;
    private String name;
    private String imageUrl;

    public SpotifyArtist(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}