-- V13: 쿠폰 테이블 생성

-- 쿠폰 테이블
CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED')),
    discount_value NUMERIC(10, 2) NOT NULL,
    min_purchase_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    usage_limit INTEGER,
    used_count INTEGER NOT NULL DEFAULT 0,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE coupons IS '쿠폰 정보';
COMMENT ON COLUMN coupons.code IS '쿠폰 코드';
COMMENT ON COLUMN coupons.name IS '쿠폰 이름';
COMMENT ON COLUMN coupons.description IS '쿠폰 설명';
COMMENT ON COLUMN coupons.discount_type IS '할인 유형 (PERCENTAGE: 퍼센트, FIXED: 고정금액)';
COMMENT ON COLUMN coupons.discount_value IS '할인 값';
COMMENT ON COLUMN coupons.min_purchase_amount IS '최소 구매 금액';
COMMENT ON COLUMN coupons.usage_limit IS '사용 제한 횟수 (NULL이면 무제한)';
COMMENT ON COLUMN coupons.used_count IS '사용된 횟수';
COMMENT ON COLUMN coupons.valid_from IS '유효 시작일';
COMMENT ON COLUMN coupons.valid_to IS '유효 종료일';
COMMENT ON COLUMN coupons.active IS '활성화 여부';

-- 회원-쿠폰 테이블 (회원이 보유한 쿠폰)
CREATE TABLE user_coupons (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    coupon_id BIGINT NOT NULL REFERENCES coupons(id) ON DELETE CASCADE,
    used BOOLEAN NOT NULL DEFAULT false,
    used_at TIMESTAMP,
    order_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, coupon_id, used)  -- 같은 쿠폰은 미사용 상태로 중복 보유 불가
);

COMMENT ON TABLE user_coupons IS '회원 보유 쿠폰';
COMMENT ON COLUMN user_coupons.user_id IS '회원 ID';
COMMENT ON COLUMN user_coupons.coupon_id IS '쿠폰 ID';
COMMENT ON COLUMN user_coupons.used IS '사용 여부';
COMMENT ON COLUMN user_coupons.used_at IS '사용 일시';
COMMENT ON COLUMN user_coupons.order_id IS '사용한 주문 ID';

-- 인덱스 생성
CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(active);
CREATE INDEX idx_user_coupons_user_id ON user_coupons(user_id);
CREATE INDEX idx_user_coupons_coupon_id ON user_coupons(coupon_id);
CREATE INDEX idx_user_coupons_used ON user_coupons(used);

