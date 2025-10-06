-- V5: image_url 컬럼 길이 증가

-- image_url 컬럼을 TEXT 타입으로 변경 (무제한 길이)
ALTER TABLE products ALTER COLUMN image_url TYPE TEXT;

COMMENT ON COLUMN products.image_url IS '상품 이미지 URL (TEXT 타입)';

