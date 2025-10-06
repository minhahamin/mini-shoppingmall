# 미니 쇼핑몰 (Mini Shopping Mall)

Spring Boot로 구현한 풀스택 쇼핑몰 프로젝트입니다.
- 배포 : https://mini-shoppingmall-production.up.railway.app/

## 🛠 기술 스택

- **Backend**: Spring Boot 3.1.5
- **Security**: Spring Security (역할 기반 인증/인가)
- **Database**: PostgreSQL
- **Migration**: Flyway (자동 스키마 관리)
- **Payment**: Stripe API (카드 결제)
- **Template Engine**: Thymeleaf + Spring Security Dialect
- **Build Tool**: Maven
- **Java Version**: 17

## ✨ 주요 기능

### 👤 회원 관리
- ✅ 회원가입 및 로그인
- ✅ Spring Security를 통한 인증/인가
- ✅ 관리자/일반회원 권한 구분
- ✅ 마이페이지 (회원정보 수정, 배송정보 관리)
- ✅ 회원 탈퇴 (비밀번호 확인)
- ✅ 비밀번호 변경

### 🛍️ 상품 기능
- ✅ 상품 관리 (등록/수정/삭제) - 관리자 전용
- ✅ 페이지네이션 (5/10/20/50개씩 보기)
- ✅ 자동 검색 기능 (debounce 적용)
- ✅ 카테고리별 필터링 (드롭다운 메뉴)
- ✅ 인기 상품 TOP 3 (판매량 기준)
- ✅ 재고 관리 (결제 시 자동 차감)
- ✅ 판매량 추적 시스템

### 💝 찜 기능
- ✅ 하트 아이콘으로 찜하기/취소
- ✅ 찜 목록 관리
- ✅ 찜한 상품 바로 상세보기

### 🛒 장바구니
- ✅ 상품 담기 (수량 선택)
- ✅ 체크박스로 개별 선택
- ✅ 선택 상품 금액 실시간 계산
- ✅ 수량 증감 기능
- ✅ 선택 상품만 주문/삭제
- ✅ 행 클릭으로 체크박스 선택

### 💳 주문 및 결제
- ✅ Stripe 결제 연동 (카드 결제)
- ✅ 주문서 작성 (배송정보 자동 입력)
- ✅ 결제 완료 시 재고 자동 차감
- ✅ 선택한 항목만 장바구니에서 제거
- ✅ 주문 내역 조회 (결제 완료 상품만)
- ✅ 주문 상세 정보 확인

### 🎨 UI/UX
- ✅ 반응형 디자인
- ✅ 메인 배너 페이지
- ✅ 카테고리 드롭다운 메뉴 (클릭 토글)
- ✅ 성공/에러 알림 메시지
- ✅ 인기 상품 배지 (1위/2위/3위)
- ✅ 하트 애니메이션 효과

## 🚀 실행 방법

### 사전 요구사항
- Java 17 이상
- Maven (또는 Maven Wrapper 사용)
- PostgreSQL 데이터베이스

### 1. 데이터베이스 설정

**PostgreSQL 데이터베이스 생성:**
```sql
CREATE DATABASE shoppingmall;
```

**application-local.properties 파일 생성:**
```properties
# src/main/resources/application-local.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shoppingmall
spring.datasource.username=postgres
spring.datasource.password=your_password

# Stripe API 키 (https://dashboard.stripe.com/test/apikeys)
stripe.api.key=sk_test_your_secret_key_here
stripe.public.key=pk_test_your_public_key_here
stripe.webhook.secret=whsec_your_webhook_secret_here
```

### 2. Stripe 결제 설정

**Stripe API 키 발급:**
1. https://stripe.com 에서 계정 생성
2. https://dashboard.stripe.com/test/apikeys 에서 테스트 API 키 복사
3. `application-local.properties`에 키 설정 (위 참고)

**테스트 카드:**
- 카드번호: `4242 4242 4242 4242`
- 만료일: 미래 날짜 (예: 12/34)
- CVC: 임의의 3자리 숫자 (예: 123)
- 우편번호: 임의의 5자리 숫자 (예: 12345)

자세한 가이드는 [STRIPE_SETUP.md](STRIPE_SETUP.md) 파일을 참고하세요.

### 3. 애플리케이션 실행

**Windows (PowerShell/CMD):**
```bash
# Maven Wrapper를 사용하여 실행
.\mvnw.cmd spring-boot:run
```

또는

```bash
# 빌드 후 실행
.\mvnw.cmd clean package
java -jar target/mini-shoppingmall-1.0.0.jar
```

**Mac/Linux:**
```bash
./mvnw spring-boot:run
```

### 4. 접속

- **메인 페이지**: http://localhost:8080
- **관리자 페이지**: http://localhost:8080/admin/products
- **마이페이지**: http://localhost:8080/mypage

## 🔑 기본 계정

### 관리자 계정
- **아이디**: admin
- **비밀번호**: 1234

### 일반 회원
- 회원가입 필요 (http://localhost:8080/register)

## 💡 주요 특징

### 1. 판매량 기반 인기 상품
- 결제 완료 시 자동으로 판매량 증가
- 메인 페이지에 TOP 3 자동 표시
- 순위 배지 및 판매 수량 표시

### 2. 스마트 장바구니
- 체크박스로 선택적 주문/삭제
- 실시간 금액 계산
- 행 클릭으로 편리한 선택
- 주문 후 선택 항목만 제거

### 3. 편리한 마이페이지
- 찜/장바구니/주문 개수 한눈에 확인
- 배송정보 저장 → 주문 시 자동 입력
- 회원정보 통합 관리

### 4. 카테고리 드롭다운
- 클릭으로 열기/닫기 (▼/▲ 토글)
- 카테고리별 상품 필터링
- 전체 페이지 일관된 UX

### 5. Stripe 결제 통합
- 안전한 카드 결제
- 결제 완료 시 자동 재고 차감
- 주문 내역 실시간 동기화

## 📁 프로젝트 구조

```
mini-shoppingmall/
├── src/main/
│   ├── java/com/shoppingmall/
│   │   ├── ShoppingMallApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── StripeConfig.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── AuthController.java
│   │   │   ├── ProductController.java
│   │   │   ├── AdminController.java
│   │   │   ├── CartController.java
│   │   │   ├── WishlistController.java
│   │   │   ├── OrderController.java
│   │   │   ├── MyPageController.java
│   │   │   └── GlobalControllerAdvice.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   ├── CartService.java
│   │   │   ├── WishlistService.java
│   │   │   ├── OrderService.java
│   │   │   ├── PaymentService.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── CartRepository.java
│   │   │   ├── CartItemRepository.java
│   │   │   ├── WishlistRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── OrderItemRepository.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Cart.java
│   │   │   ├── CartItem.java
│   │   │   ├── Wishlist.java
│   │   │   ├── Order.java
│   │   │   └── OrderItem.java
│   │   └── dto/
│   │       ├── UserRegistrationDto.java
│   │       ├── UserUpdateDto.java
│   │       └── ProductDto.java
│   └── resources/
│       ├── application.properties
│       ├── application-local.properties (Git 제외)
│       ├── db/migration/ (12개 마이그레이션 파일)
│       ├── static/
│       │   ├── css/style.css
│       │   └── js/dropdown.js
│       └── templates/ (17개 HTML 파일)
├── pom.xml
├── mvnw.cmd (Windows)
├── mvnw (Mac/Linux)
├── README.md
└── STRIPE_SETUP.md
```

## 📄 페이지 구성

### 🌐 공개 페이지
- **메인 페이지** (`/`) - 인기 상품 TOP 3, 최신 상품
- **상품 목록** (`/products`) - 검색, 카테고리 필터
- **상품 상세** (`/products/{id}`) - 찜하기, 장바구니 담기
- **로그인** (`/login`)
- **회원가입** (`/register`)

### 👤 회원 페이지 (로그인 필요)
- **마이페이지** (`/mypage`) - 찜/장바구니/주문 통합 관리
- **회원정보 수정** (`/mypage/edit`) - 이름, 이메일, 배송정보, 비밀번호 변경
- **회원 탈퇴** (`/mypage/delete`) - 비밀번호 확인 후 탈퇴
- **찜 목록** (`/wishlist`) - 찜한 상품 관리
- **장바구니** (`/cart`) - 선택 주문/삭제
- **주문서** (`/order/checkout`) - 배송정보 자동 입력
- **주문 내역** (`/order/history`) - 결제 완료 주문만 표시
- **주문 상세** (`/order/detail/{id}`)

### 🔐 관리자 페이지 (ADMIN 권한 필요)
- **상품 관리** (`/admin/products`) - 페이징, 검색, CRUD
- **상품 등록/수정** (`/admin/products/new`, `/admin/products/{id}/edit`)

## 🎯 사용 시나리오

### 일반 회원
1. 회원가입 → 로그인
2. 상품 둘러보기 (카테고리별 필터링)
3. 마음에 드는 상품 찜하기 ♥
4. 장바구니에 담기
5. 마이페이지에서 배송정보 등록
6. 선택한 상품만 주문
7. Stripe로 안전하게 결제
8. 주문 내역 확인

### 관리자
1. 관리자 계정 로그인 (admin/1234)
2. 상품 등록 (이미지, 가격, 재고 등)
3. 페이징과 검색으로 상품 관리
4. 판매 상태 변경 (판매중/판매중지)
5. 상품 수정/삭제
6. 인기 상품 자동 추적

## 📊 데이터베이스 ERD

### 주요 테이블
- **users** - 회원 정보 (role, address, phoneNumber)
- **products** - 상품 정보 (salesCount, available)
- **carts / cart_items** - 장바구니
- **wishlist** - 찜 목록
- **orders / order_items** - 주문 (cartItemIds, stripeSessionId)

### 관계
```
User 1:N Cart 1:N CartItem N:1 Product
User 1:N Wishlist N:1 Product
User 1:N Order 1:N OrderItem N:1 Product
```

## 🚀 실행 방법

### 사전 요구사항
- Java 17 이상
- Maven (또는 Maven Wrapper 사용)
- PostgreSQL 데이터베이스

### 1. 데이터베이스 설정

**PostgreSQL 데이터베이스 생성:**
```sql
CREATE DATABASE shoppingmall;
```

**application-local.properties 파일 생성:**
```properties
# src/main/resources/application-local.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shoppingmall
spring.datasource.username=postgres
spring.datasource.password=your_password

# Stripe API 키 (https://dashboard.stripe.com/test/apikeys)
stripe.api.key=sk_test_your_secret_key_here
stripe.public.key=pk_test_your_public_key_here
stripe.webhook.secret=whsec_your_webhook_secret_here
```

### 2. Stripe 결제 설정

**Stripe API 키 발급:**
1. https://stripe.com 에서 계정 생성
2. https://dashboard.stripe.com/test/apikeys 에서 테스트 API 키 복사
3. `application-local.properties`에 키 설정 (위 참고)

**테스트 카드:**
- 카드번호: `4242 4242 4242 4242`
- 만료일: 미래 날짜 (예: 12/34)
- CVC: 임의의 3자리 숫자 (예: 123)
- 우편번호: 임의의 5자리 숫자 (예: 12345)

자세한 가이드는 [STRIPE_SETUP.md](STRIPE_SETUP.md) 파일을 참고하세요.

### 3. 애플리케이션 실행

**Windows (PowerShell/CMD):**
```bash
# Maven Wrapper를 사용하여 실행
.\mvnw.cmd spring-boot:run
```

또는

```bash
# 빌드 후 실행
.\mvnw.cmd clean package
java -jar target/mini-shoppingmall-1.0.0.jar
```

**Mac/Linux:**
```bash
./mvnw spring-boot:run
```

### 4. 접속

- **메인 페이지**: http://localhost:8080
- **관리자 페이지**: http://localhost:8080/admin/products
- **마이페이지**: http://localhost:8080/mypage

## 🔑 기본 계정

### 관리자 계정
- **아이디**: admin
- **비밀번호**: 123456 (배포 시 반드시 변경 필요!)

### 일반 회원
- 회원가입 필요 (http://localhost:8080/register)

## 📄 페이지 구성

### 🌐 공개 페이지
- **메인 페이지** (`/`) - 인기 상품 TOP 3, 최신 상품
- **상품 목록** (`/products`) - 검색, 카테고리 필터
- **상품 상세** (`/products/{id}`) - 찜하기, 장바구니 담기
- **로그인** (`/login`)
- **회원가입** (`/register`)

### 👤 회원 페이지 (로그인 필요)
- **마이페이지** (`/mypage`) - 찜/장바구니/주문 통합 관리
- **회원정보 수정** (`/mypage/edit`) - 이름, 이메일, 배송정보, 비밀번호 변경
- **회원 탈퇴** (`/mypage/delete`) - 비밀번호 확인 후 탈퇴
- **찜 목록** (`/wishlist`) - 찜한 상품 관리
- **장바구니** (`/cart`) - 선택 주문/삭제
- **주문서** (`/order/checkout`) - 배송정보 자동 입력
- **주문 내역** (`/order/history`) - 결제 완료 주문만 표시
- **주문 상세** (`/order/detail/{id}`)

### 🔐 관리자 페이지 (ADMIN 권한 필요)
- **상품 관리** (`/admin/products`) - 페이징, 검색, CRUD
- **상품 등록/수정** (`/admin/products/new`, `/admin/products/{id}/edit`)

## 📁 프로젝트 구조

```
mini-shoppingmall/
├── src/main/
│   ├── java/com/shoppingmall/
│   │   ├── ShoppingMallApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── StripeConfig.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── AuthController.java
│   │   │   ├── ProductController.java
│   │   │   ├── AdminController.java
│   │   │   ├── CartController.java
│   │   │   ├── WishlistController.java
│   │   │   ├── OrderController.java
│   │   │   ├── MyPageController.java
│   │   │   └── GlobalControllerAdvice.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── ProductService.java
│   │   │   ├── CartService.java
│   │   │   ├── WishlistService.java
│   │   │   ├── OrderService.java
│   │   │   ├── PaymentService.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   ├── CartRepository.java
│   │   │   ├── CartItemRepository.java
│   │   │   ├── WishlistRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── OrderItemRepository.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Cart.java
│   │   │   ├── CartItem.java
│   │   │   ├── Wishlist.java
│   │   │   ├── Order.java
│   │   │   └── OrderItem.java
│   │   └── dto/
│   │       ├── UserRegistrationDto.java
│   │       ├── UserUpdateDto.java
│   │       └── ProductDto.java
│   └── resources/
│       ├── application.properties
│       ├── application-local.properties (Git 제외)
│       ├── db/migration/ (12개 마이그레이션 파일)
│       ├── static/
│       │   ├── css/style.css
│       │   └── js/dropdown.js
│       └── templates/ (17개 HTML 파일)
├── pom.xml
├── mvnw.cmd (Windows)
├── mvnw (Mac/Linux)
├── README.md
└── STRIPE_SETUP.md
```

## 🗄️ 데이터베이스 마이그레이션

이 프로젝트는 **Flyway**를 사용하여 데이터베이스 스키마를 자동으로 관리합니다.

**마이그레이션 파일:**
```
src/main/resources/db/migration/
├── V1__init_database.sql              # 사용자 테이블
├── V2__add_sample_data.sql            # 샘플 데이터
├── V3__add_products_table.sql         # 상품 테이블
├── V4__add_admin_user.sql             # 관리자 계정
├── V5__update_image_url_length.sql    # 이미지 URL 타입 변경
├── V6__create_cart_tables.sql         # 장바구니 테이블
├── V7__add_more_sample_products.sql   # 추가 더미 데이터 (30개)
├── V8__create_order_tables.sql        # 주문 테이블
├── V9__add_cart_item_ids_to_order.sql # 주문-장바구니 연결
├── V10__add_sales_count_to_products.sql # 판매량 컬럼
├── V11__create_wishlist_table.sql     # 찜 테이블
└── V12__add_user_address_phone.sql    # 배송정보 컬럼
```

**자동 실행:**
- 애플리케이션 시작 시 자동으로 마이그레이션 실행
- 새로운 마이그레이션 파일만 실행됨
- 마이그레이션 이력은 `flyway_schema_history` 테이블에 저장

**새로운 마이그레이션 추가:**
1. `src/main/resources/db/migration/` 폴더에 파일 생성
2. 파일명 형식: `V{버전}__(설명).sql` (예: `V13__add_new_feature.sql`)
3. 애플리케이션 재시작 시 자동 적용

## 🔒 보안 설정

### Spring Security 설정
- **공개 URL**: `/`, `/products/**`, `/login`, `/register`, `/css/**`, `/js/**`, `/images/**`
- **인증 필요**: `/cart/**`, `/wishlist/**`, `/order/**`, `/mypage/**`
- **관리자 전용**: `/admin/**`
- **CSRF**: 비활성화 (Stripe 결제 연동)
- **비밀번호 암호화**: BCrypt

### 환경 변수 보안
- `application-local.properties` - Git 제외
- 데이터베이스 비밀번호, API 키 등 민감정보 분리

## 🐛 트러블슈팅

### Maven Wrapper 권한 에러 (Mac/Linux)
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### PostgreSQL 연결 에러
- PostgreSQL 서비스가 실행 중인지 확인
- 데이터베이스명, 사용자명, 비밀번호 확인
- `application-local.properties` 파일 확인

### Stripe 결제 에러
- API 키가 올바른지 확인
- 테스트 모드 키 사용 확인 (`sk_test_`, `pk_test_`)
- 자세한 내용은 `STRIPE_SETUP.md` 참고

### 포트 충돌 (8080 포트 사용 중)
```bash
# 다른 포트로 실행
.\mvnw.cmd spring-boot:run -Dserver.port=8081
```

## 🚀 배포 방법

### 옵션 1: JAR 파일로 배포 (가장 간단)

**1. 프로젝트 빌드:**
```bash
.\mvnw.cmd clean package -DskipTests
```

**2. JAR 파일 생성 확인:**
```
target/mini-shoppingmall-1.0.0.jar
```

**3. 서버에서 실행:**
```bash
java -jar target/mini-shoppingmall-1.0.0.jar
```

**4. 백그라운드 실행 (Linux/Mac):**
```bash
nohup java -jar target/mini-shoppingmall-1.0.0.jar > app.log 2>&1 &
```

**5. 환경 변수 설정:**
```bash
# Windows
set SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/shoppingmall
set SPRING_DATASOURCE_USERNAME=postgres
set SPRING_DATASOURCE_PASSWORD=your_password
set STRIPE_API_KEY=sk_live_your_key

java -jar target/mini-shoppingmall-1.0.0.jar

# Linux/Mac
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/shoppingmall
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
export STRIPE_API_KEY=sk_live_your_key

java -jar target/mini-shoppingmall-1.0.0.jar
```

---

### 옵션 2: Railway 배포 (무료, 추천)

**1. Railway 계정 생성:**
- https://railway.app 접속
- GitHub 계정으로 로그인

**2. 새 프로젝트 생성:**
```
+ New Project → Deploy from GitHub repo → 저장소 선택
```

**3. PostgreSQL 추가:**
```
+ New → Database → Add PostgreSQL
```

**4. 환경 변수 설정:**
```
Railway Dashboard → Variables → Add Variable

SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
STRIPE_API_KEY=sk_live_your_key
STRIPE_PUBLIC_KEY=pk_live_your_key
STRIPE_WEBHOOK_SECRET=whsec_your_secret
```

**5. 자동 배포:**
- GitHub에 푸시하면 자동으로 배포됨
- Railway가 제공하는 URL로 접속 가능

---

### 옵션 3: Render 배포 (무료)

**1. Render 계정 생성:**
- https://render.com 접속
- GitHub 계정으로 로그인

**2. 새 Web Service 생성:**
```
+ New → Web Service → GitHub 저장소 연결
```

**3. 설정:**
```
Name: mini-shoppingmall
Environment: Java
Build Command: ./mvnw clean package -DskipTests
Start Command: java -jar target/mini-shoppingmall-1.0.0.jar
```

**4. PostgreSQL 추가:**
```
+ New → PostgreSQL → 데이터베이스 생성
```

**5. 환경 변수 설정:**
```
Environment → Add Environment Variable

SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=[Render PostgreSQL URL]
SPRING_DATASOURCE_USERNAME=[자동 생성됨]
SPRING_DATASOURCE_PASSWORD=[자동 생성됨]
STRIPE_API_KEY=sk_live_your_key
STRIPE_PUBLIC_KEY=pk_live_your_key
```

---

### 옵션 4: AWS EC2 배포 (프로덕션 추천)

**1. EC2 인스턴스 생성:**
```
Amazon Linux 2 또는 Ubuntu 20.04 LTS
t2.micro (프리티어 가능)
```

**2. 인스턴스 접속:**
```bash
ssh -i your-key.pem ec2-user@your-ec2-ip
```

**3. Java 17 설치:**
```bash
# Amazon Linux 2
sudo amazon-linux-extras install java-openjdk17

# Ubuntu
sudo apt update
sudo apt install openjdk-17-jdk
```

**4. PostgreSQL 설정:**
```bash
# RDS 사용 권장
# 또는 같은 EC2에 설치
sudo apt install postgresql postgresql-contrib
```

**5. JAR 파일 업로드:**
```bash
scp -i your-key.pem target/mini-shoppingmall-1.0.0.jar ec2-user@your-ec2-ip:~/
```

**6. 실행:**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-rds-endpoint:5432/shoppingmall
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
export STRIPE_API_KEY=sk_live_your_key

nohup java -jar mini-shoppingmall-1.0.0.jar > app.log 2>&1 &
```

**7. Nginx 리버스 프록시 (선택):**
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

### 옵션 5: Docker 배포

**1. Dockerfile 생성:**
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/mini-shoppingmall-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**2. Docker 이미지 빌드:**
```bash
docker build -t mini-shoppingmall .
```

**3. Docker 실행:**
```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/shoppingmall \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e STRIPE_API_KEY=sk_live_your_key \
  mini-shoppingmall
```

---

## 📝 배포 체크리스트

### 배포 전 확인사항:
- [ ] `application-local.properties`가 `.gitignore`에 있는지 확인
- [ ] 프로덕션 데이터베이스 생성
- [ ] Stripe 실제(live) API 키 발급
- [ ] 관리자 비밀번호 변경 (기본값 1234 → 강력한 비밀번호)
- [ ] CSRF 설정 검토 (현재 비활성화)
- [ ] 로그 레벨 설정 (`logging.level.root=INFO`)

### 배포 후 확인사항:
- [ ] 메인 페이지 접속 확인
- [ ] 회원가입/로그인 테스트
- [ ] 상품 주문 및 결제 테스트
- [ ] Stripe Webhook 설정 (프로덕션)
- [ ] 도메인 연결 (선택사항)
- [ ] HTTPS 인증서 설정 (Let's Encrypt)

---

## 🔧 프로덕션 설정 (application-prod.properties)

```properties
# src/main/resources/application-prod.properties
spring.profiles.active=prod

# 데이터베이스
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Flyway
spring.flyway.enabled=true

# Stripe
stripe.api.key=${STRIPE_API_KEY}
stripe.public.key=${STRIPE_PUBLIC_KEY}
stripe.webhook.secret=${STRIPE_WEBHOOK_SECRET}

# 로그
logging.level.root=INFO
logging.level.com.shoppingmall=INFO

# 서버
server.port=${PORT:8080}
```

---

## 📝 향후 개발 계획

- [ ] 상품 리뷰 및 평점 시스템
- [ ] 상품 이미지 업로드 기능
- [ ] 주문 상태 변경 (배송중, 배송완료)
- [ ] 이메일 알림 (주문 확인, 배송 알림)
- [ ] 쿠폰 및 할인 기능
- [ ] 관리자 대시보드 (매출 통계)

## 📜 라이센스

포트폴리오 프로젝트

## 👨‍💻 개발자 홍민하

**Made with ❤️ using Spring Boot**
