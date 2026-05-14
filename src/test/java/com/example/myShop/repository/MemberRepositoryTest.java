package com.example.myShop.repository;

import com.example.myShop.annotation.IntegrationTest;
import com.example.myShop.annotation.WithMockMember;
import com.example.myShop.entity.Member;
import com.example.myShop.support.IntegrationPersistenceSupport;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@IntegrationTest
@WithMockMember
class MemberRepositoryTest extends IntegrationPersistenceSupport {

    @Test
    @DisplayName("Auditing 테스트")
    void auditingTest() {
        Member newMember = persistMember();
        flushAndClear();

        Member member = memberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("create member : " + member.getCreatedBy());
        System.out.println("modify member : " + member.getModifiedBy());
    }
}