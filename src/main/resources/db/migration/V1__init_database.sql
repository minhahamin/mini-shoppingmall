-- V1: 초기 데이터베이스 스키마 생성

-- Users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- 코멘트 추가
COMMENT ON TABLE users IS '사용자 정보 테이블';
COMMENT ON COLUMN users.id IS '사용자 ID (Primary Key)';
COMMENT ON COLUMN users.username IS '사용자명 (3-20자)';
COMMENT ON COLUMN users.password IS '암호화된 비밀번호';
COMMENT ON COLUMN users.email IS '이메일 주소';
COMMENT ON COLUMN users.name IS '사용자 실명';
COMMENT ON COLUMN users.role IS '사용자 권한 (USER, ADMIN 등)';
COMMENT ON COLUMN users.created_at IS '가입 일시';

