package com.example.myShop.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayReadyResponse {

    private String tid;

    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl;
}
