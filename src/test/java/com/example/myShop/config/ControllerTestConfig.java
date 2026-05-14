package com.example.myShop.config;

import com.example.myShop.repository.MemberRepository;
import com.example.myShop.service.MemberService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class ControllerTestConfig {

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;
}
