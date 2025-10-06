-- V10: 상품에 판매 수량 컬럼 추가

-- 판매 수량 컬럼 추가
ALTER TABLE products ADD COLUMN sales_count INTEGER DEFAULT 0 NOT NULL;

-- 인덱스 생성 (판매량 정렬용)
CREATE INDEX idx_products_sales_count ON products(sales_count DESC);

COMMENT ON COLUMN products.sales_count IS '총 판매 수량 (주문 완료 기준)';

