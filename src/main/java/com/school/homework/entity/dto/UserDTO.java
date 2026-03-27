package com.school.homework.entity.dto;

import lombok.Data;

@Data // 使用 Lombok 自动生成 Getter/Setter
public class UserDTO {
    private String username;
    private String password;
}