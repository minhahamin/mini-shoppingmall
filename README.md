# 미니 쇼핑몰 (Mini Shopping Mall)

Spring Boot로 구현한 미니 쇼핑몰 프로젝트입니다.

## 기술 스택

- **Backend**: Spring Boot 3.1.5
- **Security**: Spring Security
- **Database**: H2 (In-Memory)
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 17

## 주요 기능

- ✅ 회원가입 및 로그인
- ✅ Spring Security를 통한 인증/인가
- ✅ 반응형 UI 디자인
- ✅ 메인 배너 페이지

## 실행 방법

### 사전 요구사항
- Java 17 이상
- Maven

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

### 접속 정보

- **애플리케이션**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:shoppingmall`
  - Username: `sa`
  - Password: (비어있음)

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
│           ├── static/
│           │   └── css/
│           │       └── style.css
│           └── templates/
│               ├── index.html
│               ├── login.html
│               └── register.html
└── pom.xml
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

