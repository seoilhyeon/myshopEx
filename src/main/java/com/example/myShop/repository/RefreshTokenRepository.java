package com.example.myShop.repository;

import com.example.myShop.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserKey(String userKey);

    void deleteByUserKey(String userKey);
}
