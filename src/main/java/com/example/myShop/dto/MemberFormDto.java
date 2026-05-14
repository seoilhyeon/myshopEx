package com.example.myShop.dto;

import com.example.myShop.constant.Role;
import com.example.myShop.entity.Member;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Builder
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotEmpty(message = "주소는 필수 입력 값 입니다.")
    private String address;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .address(this.address)
                .role(Role.ADMIN)
                .build();
    }
}
