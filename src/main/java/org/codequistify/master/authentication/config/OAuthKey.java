package org.codequistify.master.domain.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthKey {
    // Google
    @Value("${oauth2.google.client-id}")
    public String GOOGLE_CLIENT_ID;

    @Value("${oauth2.google.client-secret}")
    public String GOOGLE_CLIENT_SECRET;

    @Value("${oauth2.google.redirect-uri}")
    public String GOOGLE_REDIRECT_URI;

    @Value("${oauth2.google.token-uri}")
    public String GOOGLE_TOKEN_URI;

    @Value("${oauth2.google.resource-uri}")
    public String GOOGLE_RESOURCE_URI;

    // Kakao
    @Value("${oauth2.kakao.client-id}")
    public String KAKAO_CLIENT_ID;

    @Value("${oauth2.kakao.redirect-uri}")
    public String KAKAO_REDIRECT_URI;

    @Value("${oauth2.kakao.token-uri}")
    public String KAKAO_TOKEN_URI;

    @Value("${oauth2.kakao.resource-uri}")
    public String KAKAO_RESOURCE_URI;

    // Naver
    @Value("${oauth2.naver.client-id}")
    public String NAVER_CLIENT_ID;

    @Value("${oauth2.naver.client-secret}")
    public String NAVER_CLIENT_SECRET;

    @Value("${oauth2.naver.redirect-uri}")
    public String NAVER_REDIRECT_URI;

    @Value("${oauth2.naver.token-uri}")
    public String NAVER_TOKEN_URI;

    @Value("${oauth2.naver.resource-uri}")
    public String NAVER_RESOURCE_URI;

    // Github
    @Value("${oauth2.github.client-id}")
    public String GITHUB_CLIENT_ID;

    @Value("${oauth2.github.client-secret}")
    public String GITHUB_CLIENT_SECRET;

    @Value("${oauth2.github.redirect-uri}")
    public String GITHUB_REDIRECT_URI;

    @Value("${oauth2.github.token-uri}")
    public String GITHUB_TOKEN_URI;

    @Value("${oauth2.github.resource-uri}")
    public String GITHUB_RESOURCE_URI;
}
