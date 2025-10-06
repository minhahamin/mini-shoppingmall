-- V7: 더 많은 샘플 상품 추가

-- 전자제품
INSERT INTO products (name, description, price, stock, category, image_url, available, created_at, updated_at)
VALUES 
    ('아이폰 15 Pro', '최신 A17 Pro 칩셋 탑재', 1490000.00, 25, '전자제품', 'https://images.unsplash.com/photo-1678685888221-cda773a3dcdb?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('갤럭시 S24 Ultra', '200MP 카메라, S펜 지원', 1590000.00, 30, '전자제품', 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('맥북 Pro 16인치', 'M3 Max 칩, 36GB RAM', 3900000.00, 8, '전자제품', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('아이패드 Air', '10.9인치 Liquid Retina 디스플레이', 899000.00, 20, '전자제품', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('에어팟 Pro 2세대', '적응형 오디오, USB-C 충전', 359000.00, 50, '전자제품', 'https://images.unsplash.com/photo-1606841837239-c5a1a4a07af7?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('삼성 갤럭시 워치6', '건강 관리 기능 강화', 429000.00, 35, '전자제품', 'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('소니 WH-1000XM5', '업계 최고 노이즈 캔슬링', 449000.00, 15, '전자제품', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('로지텍 MX Master 3S', '프리미엄 무선 마우스', 139000.00, 40, '전자제품', 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('LG 울트라와이드 모니터 34인치', 'WQHD 해상도, 144Hz', 599000.00, 12, '전자제품', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('닌텐도 스위치 OLED', '7인치 OLED 스크린', 429000.00, 18, '전자제품', 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 의류
    ('나이키 에어포스 1', '클래식 화이트 스니커즈', 139000.00, 60, '의류', 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('아디다스 후디', '편안한 기본 후드티', 89000.00, 100, '의류', 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('리바이스 501 청바지', '오리지널 핏 데님', 129000.00, 75, '의류', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('노스페이스 다운 재킷', '구스다운 충전재, 방수', 389000.00, 25, '의류', 'https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('유니클로 히트텍 내복', '초경량 발열 소재', 39000.00, 200, '의류', 'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 가구
    ('시디즈 T50 의자', '인체공학 사무용 의자', 459000.00, 15, '가구', 'https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('이케아 말름 서랍장', '3단 화이트 서랍장', 159000.00, 22, '가구', 'https://images.unsplash.com/photo-1558211583-803a5049e8c0?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('한샘 책상 1200', '컴퓨터 데스크', 189000.00, 30, '가구', 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('일룸 소파 3인용', '패브릭 소파, 그레이', 890000.00, 5, '가구', 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('LED 플로어 스탠드', '밝기 조절 가능', 79000.00, 45, '가구', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 도서
    ('클린 코드', '로버트 C. 마틴 저', 32000.00, 100, '도서', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('이펙티브 자바', '조슈아 블로크 저', 36000.00, 80, '도서', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('스프링 부트와 AWS', '이동욱 저', 27000.00, 60, '도서', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('해리포터 전집', 'J.K. 롤링 저, 7권 세트', 98000.00, 30, '도서', 'https://images.unsplash.com/photo-1621351183012-e2f9972dd9bf?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('코스모스', '칼 세이건 저', 18000.00, 50, '도서', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 식품
    ('스타벅스 원두 파이크플레이스', '340g, 미디엄 로스트', 15900.00, 120, '식품', 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('제주 한라봉', '프리미엄 과일, 3kg', 35000.00, 40, '식품', 'https://images.unsplash.com/photo-1557800634-7bf3c7305596?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('동원참치 살코기', '100g x 10캔', 18900.00, 200, '식품', 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('프리미엄 스테이크 세트', '미국산 안심 500g', 59000.00, 15, '식품', 'https://images.unsplash.com/photo-1588168333986-5078d3ae3976?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('하겐다즈 아이스크림 4종', '바닐라, 초콜릿, 딸기, 녹차', 25000.00, 80, '식품', 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 기타
    ('다이슨 청소기 V15', '레이저로 먼지 감지', 1190000.00, 10, '기타', 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('필립스 전기면도기', '5방향 플렉스 헤드', 289000.00, 25, '기타', 'https://images.unsplash.com/photo-1499364615650-ec38552f4f34?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('루돌프 가습기', '초음파 방식, 6L 대용량', 79000.00, 60, '기타', 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('캠핑 텐트 5인용', '방수, 원터치 설치', 249000.00, 18, '기타', 'https://images.unsplash.com/photo-1478131143081-80f7f84ca84d?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('요가 매트 10mm', 'NBR 소재, 운동용', 35000.00, 90, '기타', 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400&h=300&fit=crop', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

