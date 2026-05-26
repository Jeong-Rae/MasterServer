package org.codequistify.master.shared.http;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WebRequestClient {
    private final RestTemplate restTemplate;

    public <T> T postForObject(String url, HttpEntity<?> request, Class<T> responseType) {
        return restTemplate.postForObject(url, request, responseType);
    }

    public <T> ResponseEntity<T> postForEntity(String url, HttpEntity<?> request, Class<T> responseType) {
        return restTemplate.postForEntity(url, request, responseType);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> request, Class<T> responseType) {
        return restTemplate.exchange(url, method, request, responseType);
    }
}
