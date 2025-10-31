package com.shoppingmall.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryTrackingService {
    
    private final RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${delivery.api.key:}")
    private String deliveryApiKey;
    
    @Value("${delivery.api.base-url:https://info.sweettracker.co.kr}")
    private String apiBaseUrl;
    
    /**
     * 배송 추적 정보 조회
     * @param company 배송사 코드 (CJ대한통운: 04, 한진택배: 05, 로젠택배: 06 등)
     * @param trackingNumber 송장번호
     * @return 배송 추적 정보
     */
    public DeliveryTrackingResult trackDelivery(String company, String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isEmpty()) {
            throw new IllegalArgumentException("송장번호가 없습니다");
        }
        
        try {
            // 배송사 코드 매핑
            String companyCode = mapCompanyToCode(company);
            
            // API 호출
            String url = String.format("%s/api/v1/trackingInfo?t_key=%s&t_code=%s&t_invoice=%s",
                    apiBaseUrl, deliveryApiKey, companyCode, trackingNumber);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseTrackingResponse(response.getBody(), company);
            } else {
                log.warn("배송 추적 API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("배송 추적 정보를 가져올 수 없습니다");
            }
            
        } catch (Exception e) {
            log.error("배송 추적 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("배송 추적 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 배송사 이름을 코드로 변환
     */
    private String mapCompanyToCode(String company) {
        if (company == null) {
            return "04"; // 기본값: CJ대한통운
        }
        
        Map<String, String> companyMap = new HashMap<>();
        companyMap.put("CJ대한통운", "04");
        companyMap.put("CJ", "04");
        companyMap.put("한진택배", "05");
        companyMap.put("한진", "05");
        companyMap.put("로젠택배", "06");
        companyMap.put("로젠", "06");
        companyMap.put("롯데택배", "08");
        companyMap.put("롯데", "08");
        companyMap.put("우체국택배", "01");
        companyMap.put("우체국", "01");
        companyMap.put("CU편의점택배", "46");
        companyMap.put("CU", "46");
        companyMap.put("GS25편의점택배", "47");
        companyMap.put("GS25", "47");
        companyMap.put("대신택배", "22");
        companyMap.put("대신", "22");
        companyMap.put("일양로지스", "23");
        companyMap.put("일양", "23");
        companyMap.put("경동택배", "24");
        companyMap.put("경동", "24");
        
        return companyMap.getOrDefault(company, "04");
    }
    
    /**
     * API 응답 파싱
     */
    private DeliveryTrackingResult parseTrackingResponse(String responseBody, String company) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            
            DeliveryTrackingResult result = new DeliveryTrackingResult();
            result.setCompany(company);
            result.setTrackingNumber(root.path("invoiceNo").asText());
            
            // 배송 상태 확인
            String status = root.path("complete").asText(); // "Y" 또는 "N"
            if ("Y".equals(status)) {
                result.setStatus("DELIVERED");
                result.setDelivered(true);
            } else {
                result.setStatus("SHIPPED");
                result.setDelivered(false);
            }
            
            // 배송 추적 내역
            JsonNode tracks = root.path("trackingDetails");
            List<DeliveryTrackingDetail> details = new ArrayList<>();
            
            if (tracks.isArray()) {
                for (JsonNode track : tracks) {
                    DeliveryTrackingDetail detail = new DeliveryTrackingDetail();
                    detail.setTime(track.path("timeString").asText());
                    detail.setLocation(track.path("where").asText());
                    detail.setStatus(track.path("kind").asText());
                    details.add(detail);
                }
            }
            
            result.setDetails(details);
            result.setLastStatus(details.isEmpty() ? "" : details.get(0).getStatus());
            
            return result;
            
        } catch (Exception e) {
            log.error("배송 추적 응답 파싱 오류: {}", e.getMessage(), e);
            throw new RuntimeException("배송 추적 정보를 파싱할 수 없습니다");
        }
    }
    
    /**
     * 배송 추적 결과
     */
    @lombok.Data
    public static class DeliveryTrackingResult {
        private String company;
        private String trackingNumber;
        private String status; // SHIPPED, DELIVERED
        private boolean delivered;
        private String lastStatus;
        private List<DeliveryTrackingDetail> details;
    }
    
    /**
     * 배송 추적 상세 정보
     */
    @lombok.Data
    public static class DeliveryTrackingDetail {
        private String time;
        private String location;
        private String status;
    }
    
    /**
     * 배송 추적 URL 생성 (API 키 불필요 - 각 택배사 웹사이트 링크)
     * 일반 개발자도 사용 가능한 방법
     */
    public String getTrackingUrl(String company, String trackingNumber) {
        if (company == null || trackingNumber == null || trackingNumber.isEmpty()) {
            return "#";
        }
        
        // 각 택배사별 배송 추적 URL 생성
        String normalizedCompany = company.trim();
        String url;
        
        if (normalizedCompany.contains("CJ") || normalizedCompany.contains("대한통운")) {
            // CJ대한통운
            url = String.format("https://www.cjlogistics.com/ko/tool/parcel/tracking?gnbInvcNo=%s", trackingNumber);
        } else if (normalizedCompany.contains("한진")) {
            // 한진택배
            url = String.format("https://www.hanjin.co.kr/kor/CMS/DeliveryMgr/WaybillResult.do?mCode=MN038&schLang=KR&wblnumText2=%s", trackingNumber);
        } else if (normalizedCompany.contains("로젠")) {
            // 로젠택배
            url = String.format("https://www.logen.co.kr/parcel/trace/%s/%s", trackingNumber, trackingNumber);
        } else if (normalizedCompany.contains("롯데")) {
            // 롯데택배
            url = String.format("https://www.lotteglogis.com/home/reservation/tracking/linkView?InvNo=%s", trackingNumber);
        } else if (normalizedCompany.contains("우체국")) {
            // 우체국택배
            url = String.format("https://service.epost.go.kr/trace.RetrieveDomRigiTraceList.comm?displayHeader=N&sid1=%s", trackingNumber);
        } else if (normalizedCompany.contains("CU")) {
            // CU편의점택배
            url = String.format("https://www.cvsnet.co.kr/invoice/tracking.do?invoice_no=%s", trackingNumber);
        } else if (normalizedCompany.contains("GS25") || normalizedCompany.contains("GS")) {
            // GS25편의점택배
            url = String.format("https://www.cvsnet.co.kr/invoice/tracking.do?invoice_no=%s", trackingNumber);
        } else if (normalizedCompany.contains("대신")) {
            // 대신택배
            url = String.format("https://www.daesinlogistics.co.kr/freight/freightTrack_01.asp?f_slipno=%s", trackingNumber);
        } else if (normalizedCompany.contains("일양")) {
            // 일양로지스
            url = String.format("https://www.ilyanglogis.com/functionality/freight_search.asp?slipno=%s", trackingNumber);
        } else if (normalizedCompany.contains("경동")) {
            // 경동택배
            url = String.format("https://www.kdexp.com/home/search/trackingView?billNo=%s", trackingNumber);
        } else {
            // 기본값: 통합 배송 조회 사이트
            url = String.format("https://tracker.delivery/#/%s", trackingNumber);
        }
        
        return url;
    }
    
    /**
     * API 키 없이도 사용 가능한지 확인
     */
    public boolean isApiKeyRequired() {
        return deliveryApiKey == null || deliveryApiKey.isEmpty();
    }
}

