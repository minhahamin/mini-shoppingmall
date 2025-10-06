-- V4: 관리자 계정 추가

-- 관리자 계정 추가 (username: admin, password: 1234)
-- BCrypt로 암호화된 비밀번호 (1234)
INSERT INTO users (username, password, email, name, role, created_at)
VALUES ('admin', '$2a$10$E2UPv7arXmp8v5FkVlG9Z.z5EB0vFqG3xYvW1b1OwZvQKGFB5YoL2', 'admin@shoppingmall.com', '관리자', 'ADMIN', CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 샘플 상품 데이터 추가
INSERT INTO products (name, description, price, stock, category, available, created_at, updated_at)
VALUES 
    ('노트북', '고성능 게이밍 노트북', 1500000.00, 10, '전자제품', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('무선 마우스', '인체공학적 디자인', 35000.00, 50, '전자제품', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('키보드', '기계식 키보드', 120000.00, 30, '전자제품', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('모니터', '27인치 4K 모니터', 450000.00, 15, '전자제품', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('책상', '높이 조절 가능 스탠딩 책상', 280000.00, 8, '가구', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

