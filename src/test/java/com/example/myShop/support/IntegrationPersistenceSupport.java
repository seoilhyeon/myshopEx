package com.example.myShop.support;

import com.example.myShop.entity.Member;
import com.example.myShop.fixture.MemberFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class IntegrationPersistenceSupport extends RepositoryPersistenceSupport {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Member persistEncodedMember() {
        return persistEncodedMember(nextMemberEmail());
    }

    protected Member persistEncodedMember(String email) {
        return memberRepository.save(MemberFixture.createMember(email, passwordEncoder));
    }
}
