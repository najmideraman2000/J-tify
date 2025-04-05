package com.najmi.j_tify.service;

import com.najmi.j_tify.model.SpotifyTrack;
import com.najmi.j_tify.model.SpotifyArtist;
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

    private static final String SPOTIFY_TOP_TRACKS_URL =  "https://api.spotify.com/v1/me/top/tracks";
    private static final String SPOTIFY_TOP_ARTISTS_URL = "https://api.spotify.com/v1/me/top/artists";
    private static final String SPOTIFY_SAVE_TRACK_URL = "https://api.spotify.com/v1/me/tracks";
    private static final String SPOTIFY_FOLLOW_ARTIST_URL = "https://api.spotify.com/v1/me/following?type=artist";
    private static final String SPOTIFY_PLAYBACK_URL = "https://api.spotify.com/v1/me/player/play";

    public List<SpotifyTrack> getTopTracks(String accessToken, String timeRange) {
        List<SpotifyTrack> jPopTracks = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        int offset = 0;

        while (jPopTracks.size() < 10) {
            String url = SPOTIFY_TOP_TRACKS_URL + "?limit=15&offset=" + offset + "&time_range=" + timeRange;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<SpotifyTrack> batchTracks = parseTracks(response.getBody(), accessToken);
            jPopTracks.addAll(batchTracks);

            if (batchTracks.isEmpty()) {
                break;
            }

            offset += 15;
        }

        return jPopTracks.size() > 10 ? jPopTracks.subList(0, 10) : jPopTracks;
    }

    public List<SpotifyArtist> getTopArtists(String accessToken, String timeRange) {
        List<SpotifyArtist> topArtists = new ArrayList<>();
        int offset = 0;

        while (topArtists.size() < 10) {
            String url = SPOTIFY_TOP_ARTISTS_URL + "?limit=15&offset=" + offset + "&time_range=" + timeRange;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            List<SpotifyArtist> batchArtists = parseArtists(response.getBody());
            topArtists.addAll(batchArtists);

            if (batchArtists.isEmpty()) {
                break;
            }

            offset += 15;
        }

        return topArtists.size() > 10 ? topArtists.subList(0, 10) : topArtists;
    }

    public boolean saveTrack(String accessToken, String trackId) {
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{}";
        if (trackId != null && !trackId.isEmpty()) {
            requestBody = "{\"ids\": [\"" + trackId + "\"]}";
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    SPOTIFY_SAVE_TRACK_URL,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("Error on saving track: " + e.getMessage());
            return false;
        }
    }

    public boolean followArtist(String accessToken, String artistId) {
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{}";
        if (artistId != null && !artistId.isEmpty()) {
            requestBody = "{\"ids\": [\"" + artistId + "\"]}";
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    SPOTIFY_FOLLOW_ARTIST_URL,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            System.err.println("Error on following artist: " + e.getMessage());
            return false;
        }
    }

    public String startOrResumePlayback(String accessToken, String trackId) {
        if (accessToken == null || accessToken.isEmpty()) {
            return "unauthorized";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{}";
        if (trackId != null && !trackId.isEmpty()) {
            requestBody = "{\"uris\": [\"spotify:track:" + trackId + "\"]}";
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    SPOTIFY_PLAYBACK_URL,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return "success";
            } else {
                return "error";
            }
        } catch (Exception e) {
            if (e.getMessage().contains("NO_ACTIVE_DEVICE")) {
                return "no_active_device";
            }
            System.err.println("Error starting or resuming playback: " + e.getMessage());
            return "error";
        }
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

                String trackId = trackNode.path("id").asText();
                String trackName = trackNode.path("name").asText();
                String artistName = artistNameMap.get(artistId);

                JsonNode albumImages = trackNode.path("album").path("images");
                String albumImageUrl = !albumImages.isEmpty() ? albumImages.get(0).path("url").asText() : "";

                jPopTracks.add(new SpotifyTrack(trackId, trackName, artistName, albumImageUrl));
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

    private List<SpotifyArtist> parseArtists(String responseBody) {
        List<SpotifyArtist> artists = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode items = root.path("items");

            for (JsonNode artistNode : items) {
                String artistId = artistNode.path("id").asText();
                String artistName = artistNode.path("name").asText();

                // Check the genres of the artist
                JsonNode genres = artistNode.path("genres");

                boolean isJPop = false;
                for (JsonNode genre : genres) {
                    if (genre.asText().toLowerCase().contains("j-pop")) {
                        isJPop = true;
                        break;
                    }
                }

                if (isJPop) {
                    JsonNode images = artistNode.path("images");
                    String imageUrl = !images.isEmpty() ? images.get(0).path("url").asText() : "";

                    artists.add(new SpotifyArtist(artistId, artistName, imageUrl));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Spotify response", e);
        }

        return artists;
    }
}