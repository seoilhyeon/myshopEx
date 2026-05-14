package com.example.myShop.service;

import com.example.myShop.entity.Member;
import com.example.myShop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Assert.state(!memberRepository.existsByEmail(member.getEmail()), "이미 가입된 회원입니다.");
    }
}
