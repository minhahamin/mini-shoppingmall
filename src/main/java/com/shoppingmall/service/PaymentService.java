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
        
        // 할인 금액 계산
        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (var item : order.getItems()) {
            itemsTotal = itemsTotal.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal finalAmount = itemsTotal.subtract(discountAmount);
        
        // 최종 금액이 0보다 커야 함
        if (finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0원보다 커야 합니다");
        }
        
        // 할인이 있는 경우, 각 상품 가격을 비례적으로 조정하여 할인 반영
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 할인 비율 계산
            BigDecimal discountRatio = BigDecimal.ONE.subtract(discountAmount.divide(itemsTotal, 4, java.math.RoundingMode.HALF_UP));
            
            // 주문 항목을 라인 아이템으로 변환 (할인 반영)
            for (var item : order.getItems()) {
                BigDecimal originalPrice = item.getPrice();
                BigDecimal discountedPrice = originalPrice.multiply(discountRatio).setScale(0, java.math.RoundingMode.HALF_UP);
                
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("krw")
                                .setUnitAmount(discountedPrice.longValue())
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
        } else {
            // 할인이 없는 경우 원래대로
            for (var item : order.getItems()) {
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("krw")
                                .setUnitAmount(item.getPrice().longValue())
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
        }
        
        Session session = Session.create(builder.build());
        
        return session;
    }
}

