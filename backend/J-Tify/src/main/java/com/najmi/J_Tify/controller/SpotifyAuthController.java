package com.najmi.j_tify.controller;

import com.najmi.j_tify.service.SpotifyAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SpotifyAuthController {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private final SpotifyAuthService spotifyAuthService;
    private static final String SPOTIFY_AUTH_URL = "https://accounts.spotify.com/authorize";

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @GetMapping("/login")
    public RedirectView login(@RequestParam(value = "redirect_uri", required = false) String returnUri) {
        String authUrl = SPOTIFY_AUTH_URL + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&scope=user-top-read user-library-modify"
                + (returnUri != null ? "&state=" + returnUri : "");

        return new RedirectView(authUrl);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code,
                                 @RequestParam(value = "state", required = false) String returnUri) {
        String token = spotifyAuthService.exchangeCodeForToken(code);

        return new RedirectView(returnUri != null ? returnUri + "?access_token=" + token : "/top-jpop");
    }
}