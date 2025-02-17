package com.example.urlshort.controller;

import com.example.urlshort.dto.ShortenRequest;
import com.example.urlshort.model.ShortUrl;
import com.example.urlshort.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShortUrlController {
    private String baseUrl;

    private final ShortUrlService shortUrlService;
    private static final Logger logger = LoggerFactory.getLogger(ShortUrlController.class);

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody ShortenRequest request) {
        String originalUrl = request.getUrl();

        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid URL");
        }

        ShortUrl shortUrl = shortUrlService.shortenUrl(originalUrl);
        String shortUrlResponse = baseUrl + "/api/" + shortUrl.getId();
        return ResponseEntity.ok(shortUrlResponse);
    }

    /**
     * 단축 URL 리디렉션 API
     * GET 요청: /api/{shortKey}
     * 원본 URL로 리디렉션
     */
    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortKey) {
        Optional<ShortUrl> shortUrlOpt = shortUrlService.getOriginalUrl(shortKey);

        if (shortUrlOpt.isPresent()) {
            ShortUrl shortUrl = shortUrlOpt.get();
            shortUrlService.incrementClickCount(shortKey); // 조회수 증가

            String originalUrl = shortUrl.getOriginalUrl();
            if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                originalUrl = "https://" + originalUrl; // 프로토콜 추가
            }

            URI safeUri = UriComponentsBuilder.fromUriString(originalUrl).build().toUri();
            logger.info("Redirecting {} to {}", shortKey, originalUrl);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(safeUri)
                    .build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/info/{shortKey}")
    public ResponseEntity<Map<String, Object>> getUrlInfo(@PathVariable String shortKey) {
        Optional<ShortUrl> shortUrlOpt = shortUrlService.getOriginalUrl(shortKey);

        if (shortUrlOpt.isPresent()) {
            ShortUrl shortUrl = shortUrlOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("originalUrl", shortUrl.getOriginalUrl());
            response.put("clickCount", shortUrl.getClickCount());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
