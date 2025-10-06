# 미니 쇼핑몰 (Mini Shopping Mall)

Spring Boot로 구현한 미니 쇼핑몰 프로젝트입니다.

## 기술 스택

- **Backend**: Spring Boot 3.1.5
- **Security**: Spring Security
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Payment**: Stripe
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 17

## 주요 기능

- ✅ 회원가입 및 로그인
- ✅ Spring Security를 통한 인증/인가
- ✅ 관리자/일반회원 권한 구분
- ✅ 상품 관리 (등록/수정/삭제) - 관리자 전용
- ✅ 상품 목록 및 검색 (자동 검색)
- ✅ 장바구니 기능 (체크박스 선택, 실시간 금액 계산)
- ✅ 주문 및 Stripe 결제 시스템
- ✅ 주문 내역 조회
- ✅ Flyway 데이터베이스 마이그레이션
- ✅ 반응형 UI 디자인
- ✅ 메인 배너 페이지

## 실행 방법

### 사전 요구사항
- Java 17 이상
- Maven (또는 Maven Wrapper 사용)
- PostgreSQL 데이터베이스

### 실행 명령어

**Windows (PowerShell/CMD):**
```bash
# Maven Wrapper를 사용하여 실행 (Maven 설치 불필요)
.\mvnw.cmd spring-boot:run
```

또는

```bash
# Maven Wrapper로 빌드 후 실행
.\mvnw.cmd clean package
java -jar target/mini-shoppingmall-1.0.0.jar
```

**Mac/Linux:**
```bash
# Maven Wrapper를 사용하여 실행
./mvnw spring-boot:run
```

**Maven이 설치되어 있는 경우:**
```bash
mvn spring-boot:run
```

### Stripe 결제 설정

**Stripe API 키 발급 및 설정:**

자세한 가이드는 [STRIPE_SETUP.md](STRIPE_SETUP.md) 파일을 참고하세요.

**빠른 설정:**
1. https://stripe.com 에서 계정 생성
2. https://dashboard.stripe.com/test/apikeys 에서 테스트 API 키 복사
3. `application-local.properties`에 키 설정

**테스트 카드:**
- 카드번호: `4242 4242 4242 4242`
- 만료일: 미래 날짜 (예: 12/25)
- CVC: 123

### 데이터베이스 설정

**1. PostgreSQL 데이터베이스 생성:**
```sql
CREATE DATABASE shoppingmall;
```

**2. 환경변수 설정 (두 가지 방법):**

**방법 1: application-local.properties 사용 (권장)**
```bash
# src/main/resources/application-local.properties 파일 생성
spring.datasource.url=jdbc:postgresql://localhost:5432/shoppingmall
spring.datasource.username=postgres
spring.datasource.password=your_password
```

**방법 2: 환경변수 사용**
```bash
# Windows PowerShell
$env:DB_URL="jdbc:postgresql://localhost:5432/shoppingmall"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"

# Windows CMD
set DB_URL=jdbc:postgresql://localhost:5432/shoppingmall
set DB_USERNAME=postgres
set DB_PASSWORD=your_password
```

### 접속 정보

- **애플리케이션**: http://localhost:8080

### 데이터베이스 마이그레이션

이 프로젝트는 **Flyway**를 사용하여 데이터베이스 스키마를 자동으로 관리합니다.

**마이그레이션 파일 위치:**
```
src/main/resources/db/migration/
├── V1__init_database.sql          # 초기 스키마
├── V2__add_sample_data.sql        # 샘플 데이터
└── README.md                       # 마이그레이션 가이드
```

**자동 실행:**
- 애플리케이션 시작 시 자동으로 마이그레이션 실행
- 새로운 마이그레이션 파일만 실행됨
- 마이그레이션 이력은 `flyway_schema_history` 테이블에 저장

**새로운 마이그레이션 추가:**
1. `src/main/resources/db/migration/` 폴더에 파일 생성
2. 파일명 형식: `V{버전}__(설명).sql` (예: `V3__add_products_table.sql`)
3. 애플리케이션 재시작 시 자동 적용

## 프로젝트 구조

```
mini-shoppingmall/
├── src/
│   └── main/
│       ├── java/com/shoppingmall/
│       │   ├── ShoppingMallApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── controller/
│       │   │   ├── HomeController.java
│       │   │   └── AuthController.java
│       │   ├── service/
│       │   │   ├── UserService.java
│       │   │   └── CustomUserDetailsService.java
│       │   ├── repository/
│       │   │   └── UserRepository.java
│       │   ├── entity/
│       │   │   └── User.java
│       │   └── dto/
│       │       └── UserRegistrationDto.java
│       └── resources/
│           ├── application.properties
│           ├── application-local.properties (Git 제외)
│           ├── db/
│           │   └── migration/
│           │       ├── V1__init_database.sql
│           │       ├── V2__add_sample_data.sql
│           │       └── README.md
│           ├── static/
│           │   └── css/
│           │       └── style.css
│           └── templates/
│               ├── index.html
│               ├── login.html
│               └── register.html
├── pom.xml
├── mvnw.cmd (Windows용)
├── mvnw (Mac/Linux용)
└── env.example
```

## 페이지 구성

1. **메인 페이지** (`/`)
   - 배너와 함께하는 메인 페이지
   - 헤더에 로그인/회원가입 버튼

2. **로그인 페이지** (`/login`)
   - 사용자명과 비밀번호로 로그인

3. **회원가입 페이지** (`/register`)
   - 사용자명, 이름, 이메일, 비밀번호로 회원가입

## 개발 계획

- [ ] 상품 관리 기능
- [ ] 장바구니 기능
- [ ] 주문 및 결제 기능
- [ ] 관리자 페이지

## 라이센스

MIT License

