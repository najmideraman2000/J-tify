package com.najmi.j_tify.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Objects;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private static final String SPOTIFY_TOKEN_URL =  "https://accounts.spotify.com/api/token";

    public String exchangeCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code&code=" + code +
                      "&redirect_uri=" + redirectUri +
                      "&client_id=" + clientId +
                      "&client_secret=" + clientSecret;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                SPOTIFY_TOKEN_URL,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) Objects.requireNonNull(response.getBody()).get("access_token");
        }
        return null;
    }
}
