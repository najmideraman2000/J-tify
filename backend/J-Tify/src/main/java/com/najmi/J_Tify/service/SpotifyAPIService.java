package com.najmi.j_tify.service;

import com.najmi.j_tify.model.SpotifyTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SpotifyAPIService {

    @Autowired
    private SpotifyAuthService authService;

    @Autowired
    private RestTemplate restTemplate;

    public List<SpotifyTrack> getTopTracks(String timeRange) {
        List<SpotifyTrack> jPopTracks = new ArrayList<>();
        String accessToken = authService.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        int offset = 0;

        while (jPopTracks.size() < 10) {
            String url = "https://api.spotify.com/v1/me/top/tracks?limit=15&offset=" + offset + "&time_range=" + timeRange;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            List<SpotifyTrack> batchTracks = parseTracks(response.getBody(), accessToken);
            jPopTracks.addAll(batchTracks);

            if (batchTracks.isEmpty()) {
                break;
            }

            offset += 15;
        }

        return jPopTracks.size() > 10 ? jPopTracks.subList(0, 10) : jPopTracks;
    }

    private List<SpotifyTrack> parseTracks(String responseBody, String accessToken) {
        List<SpotifyTrack> jPopTracks = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode items = root.path("items");

            Set<String> artistIds = new HashSet<>();
            Map<String, String> artistNameMap = new HashMap<>();

            for (JsonNode trackNode : items) {
                JsonNode artists = trackNode.path("artists");
                if (artists.isEmpty()) continue;

                String artistId = artists.get(0).path("id").asText();
                String artistName = artists.get(0).path("name").asText();

                artistIds.add(artistId);
                artistNameMap.put(artistId, artistName);
            }

            Set<String> jPopArtistIds = fetchJPopArtists(artistIds, accessToken);

            for (JsonNode trackNode : items) {
                if (jPopTracks.size() >= 10) break;

                JsonNode artists = trackNode.path("artists");
                if (artists.isEmpty()) continue;

                String artistId = artists.get(0).path("id").asText();
                if (!jPopArtistIds.contains(artistId)) continue;

                String trackName = trackNode.path("name").asText();
                String artistName = artistNameMap.get(artistId);

                JsonNode albumImages = trackNode.path("album").path("images");
                String albumImageUrl = !albumImages.isEmpty() ? albumImages.get(0).path("url").asText() : "";

                jPopTracks.add(new SpotifyTrack(trackName, artistName, albumImageUrl));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Spotify response", e);
        }

        return jPopTracks;
    }

    private Set<String> fetchJPopArtists(Set<String> artistIds, String accessToken) {
        if (artistIds.isEmpty()) return new HashSet<>();

        String url = "https://api.spotify.com/v1/artists?ids=" + String.join(",", artistIds);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode artistsArray = objectMapper.readTree(response.getBody()).path("artists");

            return StreamSupport.stream(artistsArray.spliterator(), false)
                    .filter(artist -> {
                        JsonNode genres = artist.path("genres");
                        for (JsonNode genre : genres) {
                            if (genre.asText().toLowerCase().contains("j-pop")) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .map(artist -> artist.path("id").asText()).collect(Collectors.toSet());
        } catch (Exception e) {
            System.err.println("Failed to fetch artist details: " + e.getMessage());
            return new HashSet<>();
        }
    }
}
