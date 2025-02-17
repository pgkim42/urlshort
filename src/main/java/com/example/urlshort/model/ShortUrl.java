package com.example.urlshort.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "short_urls")  // MongoDB 컬렉션 이름
@Getter @Setter  // Lombok: Getter/Setter 자동 생성
@NoArgsConstructor  // Lombok: 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 포함한 생성자 자동 생성
@Builder  // Lombok: Builder 패턴 적용 가능
public class ShortUrl {

    @Id
    private String id; // 단축된 URL Key (MongoDB의 기본 ID)
    private String originalUrl; // 원본 URL
    private LocalDateTime createdAt; // 생성 날짜
    private int clickCount; // 조회 수

    // 생성 시간 기본값 설정
    public ShortUrl(String id, String originalUrl) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0;
    }

    // 클릭 수 증가 메서드 (Lombok이 자동 생성하지 않음)
    public void incrementClickCount() {
        this.clickCount++;
    }
}
