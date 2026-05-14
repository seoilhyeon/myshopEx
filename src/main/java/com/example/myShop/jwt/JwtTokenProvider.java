package com.example.myShop.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    public static final String ACCESS_COOKIE_NAME = "access_token";
    public static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
    @Value("${jwt.secret:myshop-jwt-secret-key-myshop-jwt-secret-key-2026}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (IllegalArgumentException e) {
            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenPair generateToken(Authentication auth) {
        String accessToken = generateAccessToken(auth);
        String refreshToken = generateRefreshToken(auth);
        return new TokenPair(accessToken, refreshToken);
    }

    public String generateAccessToken(Authentication auth) {
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date accessTokenExpiresAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("auth", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresAt)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Authentication auth) {
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date refreshTokenExpiresAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("auth", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(refreshTokenExpiresAt)
                .signWith(key)
                .compact();
    }

    public void addJwtCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie(ACCESS_COOKIE_NAME, accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRE_TIME / 1000));

        Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public void expireJwtCookies(HttpServletResponse response) {
        Cookie expiredAccessCookie = new Cookie(ACCESS_COOKIE_NAME, "");
        expiredAccessCookie.setHttpOnly(true);
        expiredAccessCookie.setSecure(true);
        expiredAccessCookie.setPath("/");
        expiredAccessCookie.setMaxAge(0);

        Cookie expiredRefreshCookie = new Cookie(REFRESH_COOKIE_NAME, "");
        expiredRefreshCookie.setHttpOnly(true);
        expiredRefreshCookie.setSecure(true);
        expiredRefreshCookie.setPath("/");
        expiredRefreshCookie.setMaxAge(0);

        response.addCookie(expiredAccessCookie);
        response.addCookie(expiredRefreshCookie);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                 | IllegalArgumentException | io.jsonwebtoken.security.SecurityException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsEvenIfExpired(String token) {
        try {
            return getClaims(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsEvenIfExpired(token);
        String authValue = claims.get("auth", String.class);
        List<GrantedAuthority> authorities = authValue == null || authValue.isBlank()
                ? List.of()
                : List.of(authValue.split(",")).stream()
                        .filter(value -> !value.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }

    public static class TokenPair {

        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
