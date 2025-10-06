-- V12: 사용자 배송 정보 추가

ALTER TABLE users ADD COLUMN address VARCHAR(500);
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);

COMMENT ON COLUMN users.address IS '배송 주소';
COMMENT ON COLUMN users.phone_number IS '전화번호';

