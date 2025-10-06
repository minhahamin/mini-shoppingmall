# 배포 가이드 (Deployment Guide)

이 문서는 Mini Shopping Mall 애플리케이션을 프로덕션 환경에 배포하는 방법을 안내합니다.

## 📋 목차

1. [배포 전 준비사항](#배포-전-준비사항)
2. [Railway 배포 (추천)](#railway-배포-추천)
3. [Render 배포](#render-배포)
4. [AWS EC2 배포](#aws-ec2-배포)
5. [Docker 배포](#docker-배포)
6. [배포 후 설정](#배포-후-설정)

---

## 배포 전 준비사항

### 1. 프로덕션 Stripe 키 발급

**테스트 모드 → 실제 모드 전환:**

1. https://dashboard.stripe.com 접속
2. **왼쪽 상단 "테스트 모드" 토글** → **실제 모드**로 변경
3. **개발자 → API 키** 메뉴에서 실제 API 키 복사:
   - `sk_live_...` (Secret key)
   - `pk_live_...` (Publishable key)

⚠️ **주의**: 실제 결제가 진행되므로 신중하게 사용하세요!

### 2. 관리자 비밀번호 변경

**배포 전 반드시 변경:**

```sql
-- PostgreSQL에 직접 접속하여 실행
UPDATE users 
SET password = '$2a$10$새로운_bcrypt_해시값' 
WHERE username = 'admin';
```

**BCrypt 해시 생성 방법:**
```java
// Java 코드로 생성
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("새비밀번호");
System.out.println(hashedPassword);
```

또는 온라인 도구 사용: https://bcrypt-generator.com/

### 3. 보안 설정 검토

**application-prod.properties 확인:**
- [ ] 데이터베이스 URL은 환경 변수로 관리
- [ ] API 키는 환경 변수로 관리
- [ ] `spring.jpa.show-sql=false` (SQL 로그 비활성화)
- [ ] `logging.level.root=INFO` (로그 레벨 설정)

---

## Railway 배포 (추천)

**장점:**
- 무료 플랜 제공 ($5 credit/month)
- PostgreSQL 자동 프로비저닝
- GitHub 연동 자동 배포
- 간단한 설정

### 단계별 가이드

**1. Railway 계정 생성**
```
https://railway.app
→ "Start a New Project" 클릭
→ GitHub 연결
```

**2. 프로젝트 배포**
```
+ New Project
→ Deploy from GitHub repo
→ mini-shoppingmall 선택
```

**3. PostgreSQL 추가**
```
+ New
→ Database
→ Add PostgreSQL
```

**4. 환경 변수 설정**
```
프로젝트 선택 → Variables 탭

변수 추가:
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
STRIPE_API_KEY=sk_live_your_secret_key
STRIPE_PUBLIC_KEY=pk_live_your_public_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
```

**5. 배포 설정**
```
Settings 탭
→ Build Command: ./mvnw clean package -DskipTests
→ Start Command: java -jar target/mini-shoppingmall-1.0.0.jar
```

**6. 도메인 설정**
```
Settings → Networking
→ Generate Domain (무료 도메인 자동 생성)
→ 또는 Custom Domain 연결 가능
```

**7. 배포 확인**
```
Deployments 탭에서 배포 로그 확인
→ 성공 시 제공된 URL로 접속
```

---

## Render 배포

**장점:**
- 완전 무료 플랜 제공
- PostgreSQL 무료 제공 (90일 후 삭제)
- 자동 HTTPS

### 단계별 가이드

**1. Render 계정 생성**
```
https://render.com
→ Sign Up with GitHub
```

**2. PostgreSQL 생성**
```
+ New
→ PostgreSQL
→ Name: shoppingmall-db
→ Create Database
```

**3. Web Service 생성**
```
+ New
→ Web Service
→ Connect GitHub repository
```

**4. 설정**
```
Name: mini-shoppingmall
Environment: Java
Build Command: ./mvnw clean package -DskipTests
Start Command: java -jar target/mini-shoppingmall-1.0.0.jar
```

**5. 환경 변수**
```
Environment 탭

SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=[위에서 생성한 PostgreSQL Internal Database URL]
SPRING_DATASOURCE_USERNAME=[PostgreSQL Username]
SPRING_DATASOURCE_PASSWORD=[PostgreSQL Password]
STRIPE_API_KEY=sk_live_your_key
STRIPE_PUBLIC_KEY=pk_live_your_key
```

**6. 배포**
```
Create Web Service 클릭
→ 자동 빌드 및 배포 시작
→ 제공된 URL로 접속
```

---

## AWS EC2 배포

**장점:**
- 프로덕션 수준의 안정성
- 프리티어 가능 (12개월 무료)
- 완전한 제어

### 단계별 가이드

**1. EC2 인스턴스 시작**
```
AWS Console → EC2 → Launch Instance

AMI: Ubuntu 20.04 LTS
Instance Type: t2.micro (프리티어)
Security Group:
  - SSH (22) - 내 IP만 허용
  - HTTP (80) - 모든 IP
  - Custom TCP (8080) - 모든 IP
```

**2. 인스턴스 접속**
```bash
ssh -i your-key.pem ubuntu@your-ec2-public-ip
```

**3. 환경 설정**
```bash
# Java 17 설치
sudo apt update
sudo apt install -y openjdk-17-jdk

# 설치 확인
java -version
```

**4. PostgreSQL 설정 (옵션 A: RDS 사용 권장)**
```
AWS RDS → Create database
Engine: PostgreSQL
Template: Free tier
DB instance identifier: shoppingmall-db
Master username: postgres
Master password: [강력한 비밀번호]
```

**4-2. PostgreSQL 설정 (옵션 B: EC2에 설치)**
```bash
sudo apt install -y postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql

# 데이터베이스 생성
sudo -u postgres psql
CREATE DATABASE shoppingmall;
\q
```

**5. 애플리케이션 배포**
```bash
# 홈 디렉토리로 이동
cd ~

# JAR 파일 업로드 (로컬에서 실행)
scp -i your-key.pem target/mini-shoppingmall-1.0.0.jar ubuntu@your-ec2-ip:~/

# 또는 Git clone
git clone https://github.com/your-repo/mini-shoppingmall.git
cd mini-shoppingmall
./mvnw clean package -DskipTests
```

**6. 환경 변수 설정**
```bash
# 환경 변수 파일 생성
nano ~/app-env.sh

# 내용 추가
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-rds-endpoint:5432/shoppingmall
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
export STRIPE_API_KEY=sk_live_your_key
export STRIPE_PUBLIC_KEY=pk_live_your_key
export STRIPE_WEBHOOK_SECRET=whsec_your_secret

# 저장 후 적용
source ~/app-env.sh
```

**7. 애플리케이션 실행**
```bash
# 백그라운드 실행
nohup java -jar mini-shoppingmall-1.0.0.jar > app.log 2>&1 &

# 로그 확인
tail -f app.log
```

**8. systemd 서비스 설정 (자동 시작)**
```bash
sudo nano /etc/systemd/system/shoppingmall.service

# 내용:
[Unit]
Description=Mini Shopping Mall
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/mini-shoppingmall-1.0.0.jar
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/shoppingmall"
Environment="SPRING_DATASOURCE_USERNAME=postgres"
Environment="SPRING_DATASOURCE_PASSWORD=your_password"
Environment="STRIPE_API_KEY=sk_live_your_key"
Environment="STRIPE_PUBLIC_KEY=pk_live_your_key"

[Install]
WantedBy=multi-user.target

# 서비스 시작
sudo systemctl daemon-reload
sudo systemctl start shoppingmall
sudo systemctl enable shoppingmall

# 상태 확인
sudo systemctl status shoppingmall
```

**9. Nginx 리버스 프록시 설정 (선택)**
```bash
sudo apt install -y nginx

sudo nano /etc/nginx/sites-available/shoppingmall

# 내용:
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# 활성화
sudo ln -s /etc/nginx/sites-available/shoppingmall /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

**10. HTTPS 설정 (Let's Encrypt)**
```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

---

## Docker 배포

**1. Dockerfile 확인**
```dockerfile
# 프로젝트 루트의 Dockerfile 사용
```

**2. 이미지 빌드**
```bash
docker build -t mini-shoppingmall:latest .
```

**3. Docker Compose 사용 (권장)**
```yaml
# docker-compose.yml 생성
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: shoppingmall
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: your_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/shoppingmall
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: your_password
      STRIPE_API_KEY: sk_live_your_key
      STRIPE_PUBLIC_KEY: pk_live_your_key
    depends_on:
      - postgres

volumes:
  postgres_data:
```

**4. 실행**
```bash
docker-compose up -d
```

**5. 로그 확인**
```bash
docker-compose logs -f app
```

---

## 배포 후 설정

### 1. Stripe Webhook 설정

**프로덕션 웹훅 등록:**
```
https://dashboard.stripe.com → Developers → Webhooks

+ Add endpoint
URL: https://your-domain.com/order/webhook
Events to send: checkout.session.completed

생성된 Signing secret → STRIPE_WEBHOOK_SECRET 환경 변수에 설정
```

### 2. 관리자 비밀번호 변경

**로그인 후:**
```
마이페이지 → 회원정보 수정 → 비밀번호 변경
```

**또는 DB 직접 수정:**
```sql
UPDATE users 
SET password = '$2a$10$강력한_bcrypt_해시값' 
WHERE username = 'admin';
```

### 3. 도메인 연결 (선택사항)

**Railway/Render:**
- Dashboard에서 Custom Domain 설정

**AWS EC2:**
- Route 53에서 A 레코드 추가
- EC2 Elastic IP 연결

### 4. 모니터링 설정

**로그 확인:**
```bash
# Railway/Render: Dashboard의 Logs 탭
# EC2: tail -f app.log
# Docker: docker-compose logs -f
```

---

## 🔒 보안 체크리스트

배포 전 반드시 확인:

- [ ] 관리자 비밀번호 변경 (기본값 1234 사용 금지!)
- [ ] Stripe API 키가 실제(live) 모드인지 확인
- [ ] `application-local.properties`가 Git에 푸시되지 않았는지 확인
- [ ] HTTPS 설정 (Let's Encrypt 또는 클라우드 제공)
- [ ] 데이터베이스 비밀번호가 강력한지 확인
- [ ] PostgreSQL이 외부에서 직접 접근 불가능한지 확인
- [ ] 로그에 민감정보가 출력되지 않는지 확인

---

## 🐛 배포 트러블슈팅

### "Application failed to start"
```bash
# 로그 확인
# Railway: Dashboard → Logs
# Render: Dashboard → Logs
# EC2: tail -f app.log

# 주요 원인:
# 1. 데이터베이스 연결 실패 → URL, 사용자명, 비밀번호 확인
# 2. Flyway 마이그레이션 실패 → flyway_schema_history 테이블 확인
# 3. 포트 충돌 → PORT 환경 변수 확인
```

### "Stripe error"
```bash
# API 키 확인
# - sk_live_로 시작하는지 확인
# - 환경 변수가 올바르게 설정되었는지 확인

# Webhook secret 확인
# - 프로덕션 webhook secret 사용 확인
```

### "Database migration failed"
```sql
-- Flyway 이력 확인
SELECT * FROM flyway_schema_history;

-- 실패한 마이그레이션 복구
DELETE FROM flyway_schema_history WHERE success = false;
```

### 메모리 부족 에러
```bash
# Java 힙 메모리 증가
java -Xmx512m -jar mini-shoppingmall-1.0.0.jar
```

---

## 📊 성능 최적화 (선택사항)

### 1. 데이터베이스 커넥션 풀
```properties
# application-prod.properties에 추가
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 2. 로그 파일 관리
```properties
logging.file.name=logs/app.log
logging.file.max-size=10MB
logging.file.max-history=30
```

### 3. 캐싱 설정 (향후)
```properties
spring.cache.type=redis
```

---

## 💰 비용 예상

### Railway (무료 플랜)
- 월 $5 credit 제공
- PostgreSQL 포함
- 500시간 실행 가능 (약 20일)

### Render (무료 플랜)
- 완전 무료
- PostgreSQL 90일 후 삭제
- 15분 비활성 시 sleep

### AWS (프리티어)
- EC2 t2.micro: 무료 (12개월)
- RDS db.t3.micro: 무료 (12개월)
- 이후 월 $10-20 예상

---

## 🎯 추천 배포 방법

### 포트폴리오/테스트 목적
→ **Railway** 또는 **Render** (무료, 간편)

### 실제 서비스 운영
→ **AWS EC2 + RDS** (안정성, 확장성)

### 학습 목적
→ **Docker** (컨테이너 기술 학습)

---

**배포 성공 후 꼭 테스트하세요!**
✅ 회원가입/로그인
✅ 상품 조회
✅ 장바구니 담기
✅ 결제 (실제 카드 사용 주의!)
✅ 관리자 상품 관리

**문의사항이 있으시면 이슈를 등록해주세요!**

