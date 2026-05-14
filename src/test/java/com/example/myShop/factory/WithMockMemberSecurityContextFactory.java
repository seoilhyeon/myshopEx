package com.example.myShop.factory;

import com.example.myShop.annotation.WithMockMember;
import com.example.myShop.dto.security.ShopPrinciple;
import com.example.myShop.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockMemberSecurityContextFactory implements
        WithSecurityContextFactory<WithMockMember> {

    @Override
    public SecurityContext createSecurityContext(WithMockMember annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        ShopPrinciple principle = ShopPrinciple.from(Member.builder()
                .name(annotation.name())
                .role(annotation.role())
                .build());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principle,
                null,
                principle.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}
