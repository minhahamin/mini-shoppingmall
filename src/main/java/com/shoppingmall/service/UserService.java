package com.shoppingmall.service;

import com.shoppingmall.dto.UserRegistrationDto;
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
}

