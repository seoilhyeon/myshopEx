package com.example.myShop.service.oauth2;

import com.example.myShop.constant.Role;
import com.example.myShop.constant.SocialType;
import com.example.myShop.dto.security.ShopOAuth2User;
import com.example.myShop.entity.Member;
import com.example.myShop.repository.MemberRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private static Collection<? extends GrantedAuthority> toAuthorities(Role role) {
        Set<Role> roles = Set.of(role != null ? role : Role.USER);
        return roles.stream()
                .map(Role::name)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    private static String extractKakaoEmail(Map<String, Object> attributes) {
        Object kakaoAccount = attributes.get("kakao_account");
        if (!(kakaoAccount instanceof Map)) {
            return null;
        }
        Object email = ((Map<String, Object>) kakaoAccount).get("email");
        return email != null ? email.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private static String extractKakaoNickname(Map<String, Object> attributes) {
        Object properties = attributes.get("properties");
        if (!(properties instanceof Map)) {
            return null;
        }
        Object nickname = ((Map<String, Object>) properties).get("nickname");
        return nickname != null ? nickname.toString() : null;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_provider"),
                    "Unsupported provider: " + registrationId);
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String socialId = Objects.toString(attributes.get("id"), null);
        if (socialId == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"),
                    "Kakao user id not found");
        }

        String email = extractKakaoEmail(attributes);
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("email_required"),
                    "Kakao account email is required");
        }

        String nickname = extractKakaoNickname(attributes);

        Member member = memberRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, socialId);
        if (member == null) {
            Member existingByEmail = memberRepository.findByEmail(email);
            if (existingByEmail != null) {
                member = existingByEmail;
                member.linkSocial(SocialType.KAKAO, socialId);
            } else {
                String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
                member = Member.builder()
                        .email(email)
                        .name(nickname != null ? nickname : email)
                        .password(randomPassword)
                        .role(Role.USER)
                        .socialType(SocialType.KAKAO)
                        .socialId(socialId)
                        .build();
            }
            member = memberRepository.save(member);
        }

        Collection<? extends GrantedAuthority> authorities = toAuthorities(member.getRole());
        return new ShopOAuth2User(email, attributes, authorities);
    }
}

