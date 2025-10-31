package com.shoppingmall.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;
    
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // 파일 확장자 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("파일명이 없습니다");
        }
        
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex).toLowerCase();
        }
        
        // 이미지 파일만 허용
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            throw new IOException("이미지 파일만 업로드 가능합니다 (jpg, jpeg, png, gif, webp)");
        }
        
        // 저장 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 고유한 파일명 생성
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path targetPath = uploadPath.resolve(uniqueFilename);
        
        // 파일 저장
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // URL 반환 (예: /uploads/filename.jpg)
        return "/uploads/" + uniqueFilename;
    }
    
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        try {
            // URL에서 파일명 추출
            String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // 파일 삭제 실패 시 로그만 남기고 예외는 던지지 않음
            System.err.println("파일 삭제 실패: " + fileUrl + " - " + e.getMessage());
        }
    }
    
    public String getFullUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        
        // 이미 전체 URL인 경우
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }
        
        // 상대 경로인 경우
        if (filePath.startsWith("/")) {
            return baseUrl + filePath;
        }
        
        return baseUrl + "/" + filePath;
    }
}

