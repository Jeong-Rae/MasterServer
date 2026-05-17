package org.codequistify.master.global.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenSigningKey {
    @Value("${jwt.secret}")
    private String jwtSecret = "";

    private Key key;

    @PostConstruct
    public void init() {
        key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public Key value() {
        return key;
    }
}
