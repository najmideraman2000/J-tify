package com.najmi.j_tify.controller;

import com.najmi.j_tify.model.SpotifyTrack;
import com.najmi.j_tify.model.SpotifyArtist;
import com.najmi.j_tify.service.SpotifyAPIService;
import com.najmi.j_tify.service.SpotifyAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/api/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // Check if the token/session is valid (e.g., stored in session or cache)
        String accessToken = (String) request.getSession().getAttribute("access_token");

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        // Optionally, get user info from Spotify using the token
        return ResponseEntity.ok("User is logged in");
    }

    @GetMapping("/top-jpop-tracks")
    public Object getTopJPopTracks(HttpServletRequest request,
                                   @RequestParam(value = "time_range", defaultValue = "medium_term") String timeRange) {
        String accessToken = (String) request.getSession().getAttribute("access_token");

        if (accessToken == null || accessToken.isEmpty()) {
            return new RedirectView("/login?redirect_uri=http://localhost:5173/top-jpop-tracks");
        }

        List<SpotifyTrack> tracks = apiService.getTopTracks(accessToken, timeRange);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/top-jpop-artists")
    public ResponseEntity<List<SpotifyArtist>> getTopJPopArtists(HttpServletRequest request,
                                                                 @RequestParam(value = "time_range", defaultValue = "medium_term") String timeRange) {
        String accessToken = (String) request.getSession().getAttribute("access_token");
        System.out.println(accessToken);

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        List<SpotifyArtist> artists = apiService.getTopArtists(accessToken, timeRange);
        return ResponseEntity.ok(artists);
    }

    @PostMapping("/save-track")
    public ResponseEntity<String> saveTrack(HttpServletRequest request,
                                            @RequestParam("trackId") String trackId) {
        String accessToken = (String) request.getSession().getAttribute("access_token");
        boolean success = apiService.saveTrack(accessToken, trackId);

        if (success) {
            return ResponseEntity.ok("Track saved successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to save track.");
        }
    }

    @PostMapping("/follow-artist")
    public ResponseEntity<Void> followArtist(HttpServletRequest request,
                                             @RequestParam("artistId") String artistId) {
        String accessToken = (String) request.getSession().getAttribute("access_token");
        System.out.println("hello2");
        boolean success = apiService.followArtist(accessToken, artistId);
        System.out.println("hello");

        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/start-resume-playback")
    public ResponseEntity<String> startOrResumePlayback(HttpServletRequest request,
                                                        @RequestParam(value = "trackId", required = false) String trackId) {
        String accessToken = (String) request.getSession().getAttribute("access_token");
        boolean success = apiService.startOrResumePlayback(accessToken, trackId);

        if (success) {
            return ResponseEntity.ok("Playback started or resumed successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to start or resume playback.");
        }
    }
}