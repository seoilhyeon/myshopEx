package com.example.myShop.service.security;

import com.example.myShop.jwt.JwtTokenProvider;
import com.example.myShop.service.RefreshTokenService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String refreshToken = resolveTokenFromCookies(request, JwtTokenProvider.REFRESH_COOKIE_NAME);
        if (refreshToken != null) {
            try {
                String userKey = jwtTokenProvider.getClaimsEvenIfExpired(refreshToken).getSubject();
                refreshTokenService.deleteByUserKey(userKey);
            } catch (Exception ignored) {
                // Ignore invalid token and still clear cookies below.
            }
        }

        jwtTokenProvider.expireJwtCookies(response);
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
