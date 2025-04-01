package com.najmi.j_tify.controller;

import com.najmi.j_tify.model.SpotifyTrack;
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
}