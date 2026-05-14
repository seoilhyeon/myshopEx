package com.example.myShop.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.myShop.config.KakaoPayProperties;
import com.example.myShop.dto.payment.KakaoPayReadyResponse;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.Member;
import com.example.myShop.entity.Order;
import com.example.myShop.entity.OrderItem;
import com.example.myShop.fixture.ItemFixture;
import com.example.myShop.fixture.MemberFixture;
import com.example.myShop.repository.OrderRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class KakaoPayServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KakaoPayApiClient kakaoPayApiClient;

    @Mock
    private KakaoPayProperties kakaoPayProperties;

    @InjectMocks
    private KakaoPayService kakaoPayService;

    @Test
    @DisplayName("카카오페이 Ready 호출 후 tid를 주문에 저장한다")
    void readyPayment() {
        Long orderId = 1L;
        String email = "member@example.com";
        Member member = MemberFixture.createMember(email);
        Item item = ItemFixture.createItem("테스트 상품");
        OrderItem orderItem = OrderItem.create(item, 2);
        Order order = Order.createOrder(member, java.util.List.of(orderItem));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(kakaoPayProperties.getCid()).thenReturn("TC0ONETIME");
        when(kakaoPayApiClient.post(any(), any(), eq(KakaoPayReadyResponse.class)))
                .thenReturn(
                        ResponseEntity.ok(new KakaoPayReadyResponse("T1234567890", "https://redirect.pc")));

        KakaoPayReadyResponse response = kakaoPayService.readyPayment(
                orderId,
                email,
                "https://myshop.com/pay/approve",
                "https://myshop.com/pay/cancel",
                "https://myshop.com/pay/fail"
        );

        assertThat(response.getTid()).isEqualTo("T1234567890");
        assertThat(response.getNextRedirectPcUrl()).isEqualTo("https://redirect.pc");
        assertThat(order.getKakaoTid()).isEqualTo("T1234567890");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(kakaoPayApiClient).post(urlCaptor.capture(), any(), eq(KakaoPayReadyResponse.class));
        assertThat(urlCaptor.getValue()).isEqualTo(
                "https://open-api.kakaopay.com/online/v1/payment/ready");
    }
}
