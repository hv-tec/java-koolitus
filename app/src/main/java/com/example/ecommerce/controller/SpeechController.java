package com.example.ecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Häälväljund: tekst → Jutusta.ee TTS → WAV.
 * Võti jääb serverisse; brauser saab ainult heli.
 */
@RestController
@RequestMapping("/api/speak")
public class SpeechController {

    private final RestClient client;
    private final String voiceId;
    private final boolean enabled;

    public SpeechController(@Value("${jutusta.api-key}") String apiKey,
                            @Value("${jutusta.voice-id}") String voiceId) {
        this.enabled = apiKey != null && !apiKey.isBlank();
        this.voiceId = voiceId;
        this.client = RestClient.builder()
                .baseUrl("https://jutusta.ee/api/v1")
                .defaultHeader("xi-api-key", apiKey == null ? "" : apiKey)
                .build();
    }

    public record SpeakRequest(String text) {}

    @PostMapping(produces = "audio/wav")
    public ResponseEntity<byte[]> speak(@RequestBody SpeakRequest req) {
        if (!enabled) {
            return ResponseEntity.status(503).build();
        }
        String text = req.text() == null ? "" : req.text().strip();
        if (text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (text.length() > 2000) {
            text = text.substring(0, 2000);
        }
        byte[] wav = client.post()
                .uri("/text-to-speech/{voice}?output_format=wav", voiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("text", text, "voice_settings", Map.of("speed", 1.0)))
                .retrieve()
                .body(byte[].class);
        return ResponseEntity.ok()
                .header("Content-Type", "audio/wav")
                .body(wav);
    }
}
