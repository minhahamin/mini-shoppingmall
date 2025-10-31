-- V15: 샘플 쿠폰 데이터 추가

-- 신규 회원 환영 쿠폰 (10% 할인, 최소 구매 10,000원)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'WELCOME10',
    '신규 회원 환영 쿠폰',
    '신규 가입 회원을 위한 특별 할인 쿠폰입니다. 10,000원 이상 구매 시 사용 가능합니다.',
    'PERCENTAGE',
    10.00,
    10000.00,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 year',
    true,
    CURRENT_TIMESTAMP
);

-- 여름 시즌 할인 쿠폰 (5,000원 할인, 최소 구매 30,000원)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'SUMMER5000',
    '여름 특가 할인 쿠폰',
    '여름 시즌 특별 할인 쿠폰입니다. 30,000원 이상 구매 시 5,000원 할인됩니다.',
    'FIXED',
    5000.00,
    30000.00,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '6 months',
    true,
    CURRENT_TIMESTAMP
);

-- 생일 축하 쿠폰 (15% 할인, 최소 구매 20,000원)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'BIRTHDAY15',
    '생일 축하 쿠폰',
    '생일을 축하하며 제공되는 특별 할인 쿠폰입니다.',
    'PERCENTAGE',
    15.00,
    20000.00,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '3 months',
    true,
    CURRENT_TIMESTAMP
);

-- 무료배송 쿠폰 (3,000원 할인, 최소 구매 50,000원)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'FREESHIP',
    '무료배송 쿠폰',
    '대량 구매 고객을 위한 무료배송 쿠폰입니다. 50,000원 이상 구매 시 사용 가능합니다.',
    'FIXED',
    3000.00,
    50000.00,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 year',
    true,
    CURRENT_TIMESTAMP
);

-- 특별 할인 쿠폰 (20% 할인, 최소 구매 50,000원, 사용 제한 3회)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'SPECIAL20',
    '특별 할인 쿠폰',
    '특별 할인 이벤트 쿠폰입니다. 최대 3회까지 사용 가능합니다.',
    'PERCENTAGE',
    20.00,
    50000.00,
    3,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '6 months',
    true,
    CURRENT_TIMESTAMP
);

-- 첫 구매 할인 쿠폰 (7,000원 할인, 최소 구매 없음)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'FIRST7000',
    '첫 구매 할인 쿠폰',
    '첫 구매 고객을 위한 특별 할인 쿠폰입니다. 최소 구매 금액 제한이 없습니다.',
    'FIXED',
    7000.00,
    0.00,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 year',
    true,
    CURRENT_TIMESTAMP
);

-- 연말 특별 할인 쿠폰 (25% 할인, 최소 구매 100,000원)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'YEAREND25',
    '연말 특별 할인 쿠폰',
    '연말을 맞이하여 제공되는 특별 할인 쿠폰입니다. 100,000원 이상 구매 시 사용 가능합니다.',
    'PERCENTAGE',
    25.00,
    100000.00,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '6 months',
    true,
    CURRENT_TIMESTAMP
);

-- 소액 할인 쿠폰 (1,000원 할인, 최소 구매 없음)
INSERT INTO coupons (code, name, description, discount_type, discount_value, min_purchase_amount, usage_limit, used_count, valid_from, valid_to, active, created_at)
VALUES (
    'SALE1000',
    '소액 할인 쿠폰',
    '가벼운 쇼핑을 위한 소액 할인 쿠폰입니다. 최소 구매 금액 제한이 없습니다.',
    'FIXED',
    1000.00,
    0.00,
    NULL,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '1 year',
    true,
    CURRENT_TIMESTAMP
);

