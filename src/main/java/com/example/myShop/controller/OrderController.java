package com.example.myShop.controller;

import com.example.myShop.dto.OrderDto;
import com.example.myShop.dto.OrderHistoryDto;
import com.example.myShop.service.OrderService;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(sb.toString());
        }

        String email = principal.getName();
        Long orderId;
        try {
            orderId = orderService.order(orderDto, email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(orderId);
    }

    @GetMapping({"/orders", "/orders/{page}"})
    public String orderHistory(@PathVariable Optional<Integer> page, Principal principal,
            Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistoryDto> ordersHistDtoList = orderService.getOrderList(principal.getName(),
                pageable);

        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHistory";
    }

    @PostMapping("/order/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Principal principal) {
        if (!orderService.validateOrder(orderId, principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("주문 취소 권한이 없습니다.");
        }
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().body(orderId);
    }
}
