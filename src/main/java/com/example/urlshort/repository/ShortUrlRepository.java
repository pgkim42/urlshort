package com.example.urlshort.repository;

import com.example.urlshort.model.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends MongoRepository<ShortUrl, String> {
    Optional<ShortUrl> findByOriginalUrl(String originalUrl); // 원본 URL로 조회
}
