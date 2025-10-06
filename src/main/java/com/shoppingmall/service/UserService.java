package com.shoppingmall.service;

import com.shoppingmall.dto.UserRegistrationDto;
import com.shoppingmall.dto.UserUpdateDto;
import com.shoppingmall.entity.User;
import com.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User registerUser(UserRegistrationDto registrationDto) {
        // 사용자명 중복 체크
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다");
        }
        
        // 이메일 중복 체크
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }
        
        // 비밀번호 확인
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        // 사용자 생성
        User user = User.builder()
                .username(registrationDto.getUsername())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .email(registrationDto.getEmail())
                .name(registrationDto.getName())
                .role("USER")
                .build();
        
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }
    
    @Transactional
    public void updateUser(String username, UserUpdateDto updateDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 이름 업데이트
        if (updateDto.getName() != null && !updateDto.getName().isEmpty()) {
            user.setName(updateDto.getName());
        }
        
        // 이메일 업데이트
        if (updateDto.getEmail() != null && !updateDto.getEmail().isEmpty()) {
            // 이메일 중복 체크 (자신의 이메일 제외)
            userRepository.findByEmail(updateDto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getUsername().equals(username)) {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
                }
            });
            user.setEmail(updateDto.getEmail());
        }
        
        // 주소 업데이트
        if (updateDto.getAddress() != null) {
            user.setAddress(updateDto.getAddress());
        }
        
        // 전화번호 업데이트
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        
        // 비밀번호 변경
        if (updateDto.getCurrentPassword() != null && !updateDto.getCurrentPassword().isEmpty()) {
            if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다");
            }
            
            if (updateDto.getNewPassword() == null || updateDto.getNewPassword().isEmpty()) {
                throw new IllegalArgumentException("새 비밀번호를 입력해주세요");
            }
            
            if (!updateDto.getNewPassword().equals(updateDto.getConfirmPassword())) {
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다");
            }
            
            user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }
        
        userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        // 사용자 삭제 (CASCADE로 관련 데이터도 삭제됨)
        userRepository.delete(user);
    }
}

