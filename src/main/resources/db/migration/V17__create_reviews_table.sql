-- 리뷰 테이블 생성
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_item_id BIGINT,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE SET NULL,
    UNIQUE(user_id, order_item_id)  -- 같은 주문 항목에 대해 한 사용자는 하나의 리뷰만 작성 가능
);

-- 인덱스 추가
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_order_item_id ON reviews(order_item_id);

-- 상품 테이블에 평점 평균 필드 추가
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS average_rating DECIMAL(3, 2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;

