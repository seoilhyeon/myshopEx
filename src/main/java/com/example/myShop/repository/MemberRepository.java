package com.example.myShop.repository;

import com.example.myShop.constant.SocialType;
import com.example.myShop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    boolean existsByEmail(String email);

    Member findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
