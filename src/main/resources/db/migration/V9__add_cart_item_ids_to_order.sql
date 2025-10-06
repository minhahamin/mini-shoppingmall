-- V9: 주문 테이블에 장바구니 항목 ID 저장 컬럼 추가

-- 주문에 장바구니 항목 ID들을 저장 (배열로 저장)
ALTER TABLE orders ADD COLUMN cart_item_ids TEXT;

COMMENT ON COLUMN orders.cart_item_ids IS '주문한 장바구니 항목 ID들 (쉼표로 구분)';

