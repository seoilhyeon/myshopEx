package com.example.myShop.jwt;

import com.example.myShop.service.RefreshTokenService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = resolveTokenFromCookies(request, JwtTokenProvider.ACCESS_COOKIE_NAME);
        String refreshToken = resolveTokenFromCookies(request, JwtTokenProvider.REFRESH_COOKIE_NAME);

        if (accessToken != null) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                setAuthentication(jwtTokenProvider.getAuthentication(accessToken));
            } else if (jwtTokenProvider.isTokenExpired(accessToken)) {
                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
                    if (refreshTokenService.matches(authentication.getName(), refreshToken)) {
                        String reissuedAccessToken = jwtTokenProvider.generateAccessToken(authentication);
                        jwtTokenProvider.addJwtCookies(response, reissuedAccessToken, refreshToken);
                        setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.clearContext();
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                "Refresh token not found in server");
                        return;
                    }
                } else {
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Refresh token is invalid or expired");
                    return;
                }
            } else {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Authentication authentication) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String resolveTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
