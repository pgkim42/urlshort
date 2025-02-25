package com.example.urlshort.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenRequest {
    private String url;
    private String customUrl;
}
