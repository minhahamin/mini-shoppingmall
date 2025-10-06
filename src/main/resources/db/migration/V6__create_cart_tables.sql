-- V6: 장바구니 테이블 생성

-- 장바구니 테이블
CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 장바구니 항목 테이블
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE(cart_id, product_id)
);

-- 인덱스 생성
CREATE INDEX idx_carts_user_id ON carts(user_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);

-- 코멘트 추가
COMMENT ON TABLE carts IS '장바구니 테이블';
COMMENT ON COLUMN carts.id IS '장바구니 ID (Primary Key)';
COMMENT ON COLUMN carts.user_id IS '사용자 ID (Foreign Key)';

COMMENT ON TABLE cart_items IS '장바구니 항목 테이블';
COMMENT ON COLUMN cart_items.id IS '장바구니 항목 ID (Primary Key)';
COMMENT ON COLUMN cart_items.cart_id IS '장바구니 ID (Foreign Key)';
COMMENT ON COLUMN cart_items.product_id IS '상품 ID (Foreign Key)';
COMMENT ON COLUMN cart_items.quantity IS '수량';
COMMENT ON COLUMN cart_items.price IS '담은 시점의 가격';

