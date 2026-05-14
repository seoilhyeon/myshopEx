package com.example.myShop.dto.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class ShopOAuth2User implements OAuth2User, Principal, Serializable {

    private static final long serialVersionUID = 1L;

    private final String email;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public ShopOAuth2User(
            String email,
            Map<String, Object> attributes,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.email = email;
        this.attributes = attributes != null ? Collections.unmodifiableMap(attributes) : Map.of();
        this.authorities =
                authorities != null ? Collections.unmodifiableCollection(authorities) : List.of();
    }

    @Override
    public String getName() {
        return email;
    }
}

