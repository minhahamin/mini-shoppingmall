# Stripe 결제 설정 가이드

## 1. Stripe 계정 생성

1. https://stripe.com 접속
2. **"Start now"** 클릭하여 계정 생성
3. 이메일로 회원가입
4. **사업자등록번호 불필요** (테스트 모드)

## 2. API 키 발급

### Dashboard 접속
1. https://dashboard.stripe.com/test/apikeys 접속
2. **테스트 모드**인지 확인 (좌측 상단에 "TEST MODE" 표시)

### API 키 복사
화면에 2개의 키가 표시됩니다:

- **Publishable key** (공개 키)
  ```
  pk_test_51Abc...
  ```

- **Secret key** (비밀 키)
  ```
  sk_test_51Abc...
  ```
  → "Reveal test key" 클릭하여 확인

## 3. 프로젝트에 API 키 설정

### application-local.properties 수정

`src/main/resources/application-local.properties` 파일을 열고:

```properties
# Stripe API Keys
stripe.api.key=sk_test_복사한_시크릿_키
stripe.public.key=pk_test_복사한_퍼블릭_키
stripe.webhook.secret=whsec_웹훅_시크릿
```

## 4. 테스트 카드 번호

Stripe 테스트 환경에서 사용할 수 있는 카드:

### 성공 케이스
- **카드번호**: `4242 4242 4242 4242`
- **만료일**: 미래 날짜 (예: 12/25)
- **CVC**: 아무 3자리 (예: 123)
- **우편번호**: 아무 숫자

### 실패 케이스 (테스트용)
- **결제 거부**: `4000 0000 0000 0002`
- **카드 거부**: `4000 0000 0000 9995`

더 많은 테스트 카드: https://stripe.com/docs/testing

## 5. 실행 및 테스트

### 애플리케이션 시작
```bash
.\mvnw.cmd spring-boot:run
```

### 결제 테스트
1. 장바구니에 상품 담기
2. 체크박스 선택
3. "주문하기" 클릭
4. 배송 정보 입력
5. "Stripe 결제하기" 클릭
6. **Stripe 결제 페이지**로 이동 (새 탭)
7. 테스트 카드 `4242 4242 4242 4242` 입력
8. 결제 완료!

## 6. Webhook 설정 (선택사항)

실시간 결제 상태 업데이트를 위해:

1. https://dashboard.stripe.com/test/webhooks
2. "Add endpoint" 클릭
3. Endpoint URL: `http://localhost:8080/webhook/stripe`
4. Events: `checkout.session.completed` 선택
5. Webhook signing secret 복사 → `stripe.webhook.secret`에 설정

## 7. 프로덕션 배포 시

### ⚠️ 중요: API 키 보안

- `application-local.properties`는 **절대 Git에 커밋하지 마세요!**
- `.gitignore`에 이미 추가되어 있음
- 프로덕션에서는 **환경변수** 사용 권장:
  ```bash
  STRIPE_SECRET_KEY=sk_live_...
  STRIPE_PUBLIC_KEY=pk_live_...
  ```

## 문제 해결

### "Invalid API Key" 오류
- API 키가 정확한지 확인
- 테스트 모드 키인지 확인 (`sk_test_`, `pk_test_`로 시작)
- application-local.properties에 제대로 저장되었는지 확인

### 결제 페이지가 안 열림
- 콘솔에서 Stripe 관련 오류 메시지 확인
- API 키가 설정되었는지 확인

### 더 많은 도움말
- Stripe 공식 문서: https://stripe.com/docs
- Stripe Java SDK: https://stripe.com/docs/api/java

