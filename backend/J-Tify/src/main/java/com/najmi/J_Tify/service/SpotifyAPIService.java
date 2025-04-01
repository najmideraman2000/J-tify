package com.najmi.j_tify.service;

import com.najmi.j_tify.model.SpotifyTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyAPIService {

    @Autowired
    private SpotifyAuthService authService;

    public List<SpotifyTrack> getTopTracks(String timeRange) {
        String url = "https://api.spotify.com/v1/me/top/tracks?limit=30&time_range=" + timeRange;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authService.getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseTracks(response.getBody(), authService.getAccessToken());
    }

    private List<SpotifyTrack> parseTracks(String responseBody, String accessToken) {
        List<SpotifyTrack> jPopTracks = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode items = root.path("items");

            for (JsonNode trackNode : items) {
                if (jPopTracks.size() >= 10) break;

                String name = trackNode.path("name").asText();
                JsonNode artists = trackNode.path("artists");

                if (artists.isEmpty()) continue;

                String artistName = artists.get(0).path("name").asText();
                String artistId = artists.get(0).path("id").asText();

                if (!isJPopArtist(artistId, accessToken, restTemplate)) continue;

                JsonNode albumImages = trackNode.path("album").path("images");
                String albumImageUrl = !albumImages.isEmpty() ? albumImages.get(0).path("url").asText() : "";

                jPopTracks.add(new SpotifyTrack(name, artistName, albumImageUrl));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Spotify response", e);
        }

        return jPopTracks;
    }

    private boolean isJPopArtist(String artistId, String accessToken, RestTemplate restTemplate) {
        String url = "https://api.spotify.com/v1/artists/" + artistId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode artistJson = objectMapper.readTree(response.getBody());

            JsonNode genres = artistJson.path("genres");
            for (JsonNode genre : genres) {
                if (genre.asText().toLowerCase().contains("j-pop")) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch artist details: " + e.getMessage());
        }
        return false;
    }
}
