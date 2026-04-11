package com.school.homework.service;

import com.school.homework.common.Result;
import com.school.homework.entity.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id); // 新增根据 id 查询用户的方法 [cite: 65]
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);}