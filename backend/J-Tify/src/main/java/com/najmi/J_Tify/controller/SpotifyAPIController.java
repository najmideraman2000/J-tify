package com.najmi.j_tify.controller;

import com.najmi.j_tify.model.SpotifyTrack;
import com.najmi.j_tify.model.SpotifyArtist;
import com.najmi.j_tify.service.SpotifyAPIService;
import com.najmi.j_tify.service.SpotifyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SpotifyAPIController {

    @Autowired
    private SpotifyAPIService apiService;

    @Autowired
    private SpotifyAuthService authService;

    @GetMapping("/top-jpop")
    public Object getTopJPopTracks(@RequestParam(value = "time_range", defaultValue = "medium_term") String timeRange) {
        String accessToken = authService.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            return new RedirectView("/login?redirect_uri=/top-jpop");
        }

        List<SpotifyTrack> tracks = apiService.getTopTracks(timeRange);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/top-jpop-artists")
    public ResponseEntity<List<SpotifyArtist>> getTopJPopArtists(@RequestParam(value = "time_range", defaultValue = "medium_term") String timeRange) {
        String accessToken = authService.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        List<SpotifyArtist> artists = apiService.getTopArtists(timeRange);
        return ResponseEntity.ok(artists);
    }

    @PostMapping("/save-track")
    public ResponseEntity<String> saveTrack(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestParam("trackId") String trackId) {
        String accessToken = authorizationHeader.replace("Bearer ", ""); // Extract token
        boolean success = apiService.saveTrack(accessToken, trackId);

        if (success) {
            return ResponseEntity.ok("Track saved successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to save track.");
        }
    }

    @PostMapping("/save-artist")
    public ResponseEntity<Void> saveArtist(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestParam("artistId") String artistId) {
        String accessToken = authorizationHeader.replace("Bearer ", ""); // Extract token
        System.out.println(artistId);
        boolean success = apiService.saveArtist(accessToken, artistId); // Call service to save artist

        if (success) {
            System.out.println("Artist saved successfully!");
            return ResponseEntity.noContent().build(); // Return 204 No Content on success
        } else {
            System.out.println("Failed to save artist.");
            return ResponseEntity.badRequest().body(null); // Return 400 Bad Request on failure
        }
    }
}