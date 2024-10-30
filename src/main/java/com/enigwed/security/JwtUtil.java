package com.enigwed.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.enigwed.dto.JwtClaim;
import com.enigwed.entity.UserCredential;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    @Value("${com.enigwed.jwt-app-name}")
    private String issuer;

    @Value("${com.enigwed.jwt-expiration}")
    private long tokenExpiration;

    @Value("${com.enigwed.jwt-secret}")
    private String jwtSecret;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC512(jwtSecret.getBytes());
    }

    public String generateToken(UserCredential user) {
        try {
            return JWT
                    .create()
                    .withIssuer(issuer)
                    .withSubject(user.getId())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(tokenExpiration))
                    .withClaim("email", user.getUsername())
                    .withClaim("role", user.getRole().name())
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            log.error("Invalid while creating jwt token : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private DecodedJWT getDecodedJWT(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            return jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("JWT verification failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error decoding JWT: {}", e.getMessage());
            throw new RuntimeException("Failed to decode JWT", e);
        }
    }

    public boolean verifyJwtToken(String token) {
        try {
            DecodedJWT decodedJWT = getDecodedJWT(token);

            if (decodedJWT.getExpiresAt() == null || decodedJWT.getExpiresAt().before(new Date())) {
                log.error("JWT token is expired");
                return false;
            }

            if (!decodedJWT.getIssuer().equals(issuer)) {
                log.error("Invalid issuer: expected {}, got {}", issuer, decodedJWT.getIssuer());
                return false;
            }

            return true;

        } catch (JWTVerificationException e) {
            log.error("Invalid Verification JWT: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error verifying JWT: {}", e.getMessage());
            return false;
        }
    }

    public JwtClaim getUserInfoByToken(String token) {
        try {
            DecodedJWT decodedJWT = getDecodedJWT(token);
            return JwtClaim.builder()
                    .userId(decodedJWT.getSubject())
                    .email(decodedJWT.getClaim("email").asString())
                    .role(decodedJWT.getClaim("role").asString())
                    .build();
        } catch (JWTVerificationException e) {
            log.error("Invalid Verification info user JWT : {}", e.getMessage());
            return null;
        }
    }

    public JwtClaim getUserInfoByHeader(String headerAuth) {
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        return getUserInfoByToken(token);
    }
}
