package com.example.myShop.entity;

import com.example.myShop.constant.Role;
import com.example.myShop.constant.SocialType;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "t_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_social", columnNames = {"social_type", "social_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Column(name = "social_id")
    private String socialId;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    private Member(String name, String email, SocialType socialType, String socialId, String password,
            String address, Role role) {
        this.name = name;
        this.email = email;
        this.socialType = socialType;
        this.socialId = socialId;
        this.password = password;
        this.address = address;
        this.role = role != null ? role : Role.USER;
    }

    public boolean isSocialMember() {
        return socialType != null;
    }

    public void linkSocial(SocialType socialType, String socialId) {
        this.socialType = socialType;
        this.socialId = socialId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member)) {
            return false;
        }
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
