-- V14: 주문 테이블에 쿠폰 관련 필드 추가

ALTER TABLE orders ADD COLUMN user_coupon_id BIGINT;
ALTER TABLE orders ADD COLUMN discount_amount NUMERIC(10, 2) DEFAULT 0;

COMMENT ON COLUMN orders.user_coupon_id IS '사용한 쿠폰 ID';
COMMENT ON COLUMN orders.discount_amount IS '할인 금액';

