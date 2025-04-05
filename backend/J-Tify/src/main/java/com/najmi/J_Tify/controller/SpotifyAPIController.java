package com.najmi.j_tify.controller;

import com.najmi.j_tify.model.SpotifyTrack;
import com.najmi.j_tify.model.SpotifyArtist;
import com.najmi.j_tify.service.SpotifyAPIService;
import com.najmi.j_tify.service.SpotifyAuthService;
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

    @GetMapping("/top-jpop-tracks")
    public Object getTopJPopTracks(@RequestParam(value = "time_range", defaultValue = "medium_term") String timeRange) {
        String accessToken = authService.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            return new RedirectView("/login?redirect_uri=/top-jpop-tracks");
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
        String accessToken = authorizationHeader.replace("Bearer ", "");
        boolean success = apiService.saveTrack(accessToken, trackId);

        if (success) {
            return ResponseEntity.ok("Track saved successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to save track.");
        }
    }

    @PostMapping("/follow-artist")
    public ResponseEntity<Void> followArtist(@RequestHeader("Authorization") String authorizationHeader,
                                             @RequestParam("artistId") String artistId) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        boolean success = apiService.followArtist(accessToken, artistId);

        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/start-resume-playback")
    public ResponseEntity<String> startOrResumePlayback(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestParam(value = "trackId", required = false) String trackId) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        boolean success = apiService.startOrResumePlayback(accessToken, trackId);

        if (success) {
            return ResponseEntity.ok("Playback started or resumed successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to start or resume playback.");
        }
    }
}