package com.example.myShop.service.payment;

import com.example.myShop.config.KakaoPayProperties;
import com.example.myShop.dto.payment.KakaoPayReadyRequest;
import com.example.myShop.dto.payment.KakaoPayReadyResponse;
import com.example.myShop.entity.Order;
import com.example.myShop.entity.OrderItem;
import com.example.myShop.repository.OrderRepository;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";

    private final OrderRepository orderRepository;
    private final KakaoPayApiClient kakaoPayApiClient;
    private final KakaoPayProperties kakaoPayProperties;

    @Transactional
    public KakaoPayReadyResponse readyPayment(
            Long orderId,
            String email,
            String approvalUrl,
            String cancelUrl,
            String failUrl) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        validateOrderOwner(order, email);

        KakaoPayReadyRequest request = createReadyRequest(order, email, approvalUrl, cancelUrl,
                failUrl);
        ResponseEntity<KakaoPayReadyResponse> response = kakaoPayApiClient.post(
                KAKAO_PAY_READY_URL,
                request,
                KakaoPayReadyResponse.class);
        KakaoPayReadyResponse readyResponse = response.getBody();

        if (readyResponse == null || readyResponse.getTid() == null) {
            throw new IllegalStateException("카카오페이 Ready 응답에 tid가 없습니다.");
        }

        order.updateKakaoTid(readyResponse.getTid());
        return readyResponse;
    }

    private KakaoPayReadyRequest createReadyRequest(
            Order order,
            String email,
            String approvalUrl,
            String cancelUrl,
            String failUrl) {
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty()) {
            throw new IllegalStateException("주문 항목이 없는 주문은 결제를 준비할 수 없습니다.");
        }

        int quantity = orderItems.stream()
                .mapToInt(OrderItem::getCount)
                .sum();

        return KakaoPayReadyRequest.builder()
                .cid(kakaoPayProperties.getCid())
                .partnerOrderId(String.valueOf(order.getId()))
                .partnerUserId(email)
                .itemName(createItemName(orderItems))
                .quantity(quantity)
                .totalAmount(order.getTotalPrice())
                .taxFreeAmount(0)
                .approvalUrl(approvalUrl)
                .cancelUrl(cancelUrl)
                .failUrl(failUrl)
                .build();
    }

    private String createItemName(List<OrderItem> orderItems) {
        String firstItemName = orderItems.get(0).getItem().getItemName();
        int extraCount = orderItems.size() - 1;
        if (extraCount <= 0) {
            return firstItemName;
        }
        return firstItemName + " 외 " + extraCount + "건";
    }

    private void validateOrderOwner(Order order, String email) {
        if (!order.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("해당 주문의 결제 권한이 없습니다.");
        }
    }
}
