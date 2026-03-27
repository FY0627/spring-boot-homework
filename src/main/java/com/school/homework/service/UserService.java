package com.school.homework.service;

import com.school.homework.common.Result;
import com.school.homework.entity.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
}