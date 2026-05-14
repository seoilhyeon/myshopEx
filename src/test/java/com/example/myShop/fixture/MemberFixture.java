package com.example.myShop.fixture;

import com.example.myShop.constant.Role;
import com.example.myShop.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberFixture {

    private static final String DEFAULT_NAME = "연초코";
    private static final String DEFAULT_EMAIL = "choco@email.com";
    private static final String DEFAULT_ADDRESS = "서울시 성동구 응봉동";
    private static final String DEFAULT_PASSWORD = "12345678";
    private static final Role DEFAULT_ROLE = Role.USER;

    private MemberFixture() {
    }

    public static Member createMember() {
        return createMember(DEFAULT_EMAIL);
    }

    public static Member createMember(PasswordEncoder passwordEncoder) {
        return createMember(DEFAULT_EMAIL, passwordEncoder);
    }

    public static Member createMember(String email) {
        return createMember(DEFAULT_NAME, email, DEFAULT_ADDRESS, DEFAULT_PASSWORD, DEFAULT_ROLE);
    }

    public static Member createMember(String email, PasswordEncoder passwordEncoder) {
        return createMember(
                DEFAULT_NAME,
                email,
                DEFAULT_ADDRESS,
                passwordEncoder.encode(DEFAULT_PASSWORD),
                DEFAULT_ROLE
        );
    }

    public static Member createMember(
            String name,
            String email,
            String address,
            String password,
            Role role
    ) {
        return Member.builder()
                .name(name)
                .email(email)
                .address(address)
                .password(password)
                .role(role)
                .build();
    }
}
