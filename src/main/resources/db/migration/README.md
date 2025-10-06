# Flyway Database Migration

이 디렉토리는 Flyway를 사용한 데이터베이스 마이그레이션 스크립트를 관리합니다.

## 파일 명명 규칙

Flyway는 다음과 같은 명명 규칙을 따릅니다:

```
V{버전}__(설명).sql
```

예시:
- `V1__init_database.sql` - 초기 스키마
- `V2__add_sample_data.sql` - 샘플 데이터
- `V3__add_products_table.sql` - 상품 테이블 추가

## 마이그레이션 작성 규칙

1. **버전 번호는 순차적으로 증가**
   - V1, V2, V3, ...

2. **한 번 실행된 마이그레이션은 수정하지 않음**
   - 이미 적용된 스크립트는 변경하지 마세요
   - 수정이 필요하면 새로운 버전을 추가하세요

3. **롤백은 별도 스크립트로 작성**
   - 예: `V4__rollback_products.sql`

4. **반복 가능한 스크립트 (Repeatable)**
   - 파일명: `R__(설명).sql`
   - 뷰, 프로시저 등에 사용

## 새로운 마이그레이션 추가 방법

### 1. 테이블 추가 예시
```sql
-- V3__add_products_table.sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. 컬럼 추가 예시
```sql
-- V4__add_user_phone.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### 3. 데이터 수정 예시
```sql
-- V5__update_user_roles.sql
UPDATE users SET role = 'PREMIUM' WHERE created_at < '2024-01-01';
```

## 마이그레이션 상태 확인

Flyway는 `flyway_schema_history` 테이블에 마이그레이션 기록을 저장합니다.

```sql
SELECT * FROM flyway_schema_history;
```

## 주의사항

⚠️ **운영 환경에서는 반드시 백업 후 마이그레이션을 실행하세요!**

- 테스트 환경에서 먼저 검증
- 데이터베이스 백업 필수
- 마이그레이션 실패 시 롤백 계획 수립

