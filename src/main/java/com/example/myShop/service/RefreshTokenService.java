package com.example.myShop.service;

import com.example.myShop.entity.RefreshToken;
import com.example.myShop.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveOrUpdate(String userKey, String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserKey(userKey)
                .orElseGet(() -> RefreshToken.builder()
                        .userKey(userKey)
                        .tokenValue(refreshTokenValue)
                        .build());
        refreshToken.updateTokenValue(refreshTokenValue);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public boolean matches(String userKey, String refreshTokenValue) {
        return refreshTokenRepository.findByUserKey(userKey)
                .map(savedToken -> savedToken.getTokenValue().equals(refreshTokenValue))
                .orElse(false);
    }

    public void deleteByUserKey(String userKey) {
        refreshTokenRepository.deleteByUserKey(userKey);
    }
}
