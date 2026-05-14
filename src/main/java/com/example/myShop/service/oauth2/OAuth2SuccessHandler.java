package com.example.myShop.service.oauth2;

import com.example.myShop.jwt.JwtTokenProvider;
import com.example.myShop.jwt.JwtTokenProvider.TokenPair;
import com.example.myShop.service.RefreshTokenService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        TokenPair tokenPair = jwtTokenProvider.generateToken(authentication);
        refreshTokenService.saveOrUpdate(authentication.getName(), tokenPair.getRefreshToken());
        jwtTokenProvider.addJwtCookies(response, tokenPair.getAccessToken(),
                tokenPair.getRefreshToken());

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
