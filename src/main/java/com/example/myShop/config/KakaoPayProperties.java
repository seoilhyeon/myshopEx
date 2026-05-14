package com.example.myShop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakao.pay")
public class KakaoPayProperties {

    private String cid;
    private String secretKey;
}
