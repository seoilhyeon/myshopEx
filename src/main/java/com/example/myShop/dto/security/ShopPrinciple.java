package com.example.myShop.dto.security;

import com.example.myShop.constant.Role;
import com.example.myShop.entity.Member;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopPrinciple implements UserDetails {

    private String email;
    private String password;
    private String name;
    private String address;
    private Collection<? extends GrantedAuthority> authorities;

    public static ShopPrinciple from(Member member) {
        return new ShopPrinciple(
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getAddress(),
                Set.of(member.getRole()).stream()
                        .map(Role::name)
                        .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
