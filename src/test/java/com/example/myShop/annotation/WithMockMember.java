package com.example.myShop.annotation;

import com.example.myShop.constant.Role;
import com.example.myShop.factory.WithMockMemberSecurityContextFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockMemberSecurityContextFactory.class)
public @interface WithMockMember {

    String name() default "choco";

    Role role() default Role.USER;
}
