package com.example.myShop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "user_key", nullable = false, unique = true, length = 100)
    private String userKey;

    @Column(name = "token_value", nullable = false, length = 1000)
    private String tokenValue;

    @Builder
    private RefreshToken(String userKey, String tokenValue) {
        this.userKey = userKey;
        this.tokenValue = tokenValue;
    }

    public void updateTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
