package com.example.myShop.service.payment;

import com.example.myShop.config.KakaoPayProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoPayApiClient {

    private final RestTemplate restTemplate;
    private final KakaoPayProperties kakaoPayProperties;

    public KakaoPayApiClient(
            RestTemplateBuilder restTemplateBuilder,
            KakaoPayProperties kakaoPayProperties
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.kakaoPayProperties = kakaoPayProperties;
    }

    public <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + kakaoPayProperties.getSecretKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
