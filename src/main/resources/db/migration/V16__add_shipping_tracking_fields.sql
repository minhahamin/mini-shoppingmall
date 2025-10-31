-- 배송 추적 관련 필드 추가
ALTER TABLE orders 
ADD COLUMN tracking_company VARCHAR(50),
ADD COLUMN tracking_number VARCHAR(100),
ADD COLUMN shipped_at TIMESTAMP,
ADD COLUMN delivered_at TIMESTAMP;

-- 인덱스 추가 (송장번호로 검색)
CREATE INDEX idx_orders_tracking_number ON orders(tracking_number);

