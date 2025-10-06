package com.shoppingmall.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}

