-- V3: 상품 테이블 생성

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    category VARCHAR(50) NOT NULL DEFAULT '기타',
    available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_available ON products(available);
CREATE INDEX idx_products_name ON products(name);

-- 코멘트 추가
COMMENT ON TABLE products IS '상품 정보 테이블';
COMMENT ON COLUMN products.id IS '상품 ID (Primary Key)';
COMMENT ON COLUMN products.name IS '상품명';
COMMENT ON COLUMN products.description IS '상품 설명';
COMMENT ON COLUMN products.price IS '상품 가격';
COMMENT ON COLUMN products.stock IS '재고 수량';
COMMENT ON COLUMN products.image_url IS '상품 이미지 URL';
COMMENT ON COLUMN products.category IS '상품 카테고리';
COMMENT ON COLUMN products.available IS '판매 가능 여부';
COMMENT ON COLUMN products.created_at IS '생성 일시';
COMMENT ON COLUMN products.updated_at IS '수정 일시';

