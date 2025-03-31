package com.najmi.j_tify.controller;

import com.najmi.j_tify.model.SpotifyTrack;
import com.najmi.j_tify.service.SpotifyAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SpotifyAPIController {

    @Autowired
    private SpotifyAPIService apiService;

    @GetMapping("/top-jpop")
    public List<SpotifyTrack> getTopJPopTracks() {
        return apiService.getTopTracks();
    }
}
