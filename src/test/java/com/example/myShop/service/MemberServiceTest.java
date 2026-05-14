package com.example.myShop.service;

import static com.example.myShop.fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.myShop.annotation.IntegrationTest;
import com.example.myShop.entity.Member;
import com.example.myShop.support.IntegrationPersistenceSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class MemberServiceTest extends IntegrationPersistenceSupport {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    void saveMemberTest() {
        Member member = createMember(nextMemberEmail(), passwordEncoder);

        Member savedMember = memberService.saveMember(member);

        assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(savedMember.getName()).isEqualTo(member.getName());
        assertThat(savedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(savedMember.getAddress()).isEqualTo(member.getAddress());
        assertThat(savedMember.getRole()).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    void saveDuplicateMemberTest() {
        String email = nextMemberEmail();
        persistEncodedMember(email);
        Member member = createMember(email, passwordEncoder);

        assertThatThrownBy(() -> memberService.saveMember(member))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 가입된 회원입니다.");
    }
}
