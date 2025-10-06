package com.shoppingmall.service;

import com.shoppingmall.entity.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    @Value("${stripe.public.key}")
    private String stripePublicKey;
    
    private final OrderService orderService;
    
    public String getPublicKey() {
        return stripePublicKey;
    }
    
    // Stripe Checkout 세션 생성
    public Session createCheckoutSession(Order order, String successUrl, String cancelUrl) throws StripeException {
        
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putMetadata("orderId", order.getId().toString())
                .putMetadata("orderNumber", order.getOrderNumber());
        
        // 주문 항목을 라인 아이템으로 변환
        for (var item : order.getItems()) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("krw")
                            .setUnitAmount(item.getPrice().longValue()) // KRW는 최소 단위이므로 그대로 사용
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getProductName())
                                    .build()
                            )
                            .build()
                    )
                    .build();
            builder.addLineItem(lineItem);
        }
        
        Session session = Session.create(builder.build());
        
        return session;
    }
}

