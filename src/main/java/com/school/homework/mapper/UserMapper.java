package com.school.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.homework.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 必须添加该注解，否则 Spring 找不到该接口 [cite: 47]
public interface UserMapper extends BaseMapper<User> {
}