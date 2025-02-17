package com.example.urlshort.service;

import com.example.urlshort.model.ShortUrl;
import com.example.urlshort.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Transactional
    public ShortUrl shortenUrl(String originalUrl) {
        // 기존에 단축된 URL이 있으면 재사용
        Optional<ShortUrl> existingShortUrl = shortUrlRepository.findByOriginalUrl(originalUrl);
        if (existingShortUrl.isPresent()) {
            return existingShortUrl.get();
        }

        // 새로운 단축 URL 생성
        String shortKey = generateShortKey(originalUrl);
        ShortUrl shortUrl = new ShortUrl(shortKey, originalUrl);
        return shortUrlRepository.save(shortUrl);
    }

    public Optional<ShortUrl> getOriginalUrl(String shortKey) {
        return shortUrlRepository.findById(shortKey);
    }

    @Transactional
    public void incrementClickCount(String shortKey) {
        Optional<ShortUrl> shortUrlOpt = shortUrlRepository.findById(shortKey);
        shortUrlOpt.ifPresent(shortUrl -> {
            shortUrl.incrementClickCount();
            shortUrlRepository.save(shortUrl);
        });
    }

    private String generateShortKey(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));

            // Base62 문자 리스트 (62개)
            String base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                int index = (hash[i] & 0xFF) % 62; // 0~61 사이 값으로 변환
                sb.append(base62.charAt(index));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }
}
