package com.school.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.homework.common.Result;
import com.school.homework.common.ResultCode;
import com.school.homework.entity.User;
import com.school.homework.entity.dto.UserDTO;
import com.school.homework.mapper.UserMapper;
import com.school.homework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 查询该用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 组装实体对象
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());

        // 3. 插入数据库
        userMapper.insert(user);
        return Result.success("注册成功!");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 校验密码是否正确
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 模拟返回一个 Token
        return Result.success("Bearer mock-token-123456");
    }

    @Override
    public Result<String> getUserById(Long id) {
        // 使用 MyBatis-Plus 内置的根据 ID 查询
        User user = userMapper.selectById(id);

        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        // 注意：实际开发中通常不直接返回密码，这里为了演示简单返回提示信息
        return Result.success("查询成功, 正在返回 ID为" + id + "的用户信息: " + user.getUsername());
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象（参数1：当前页码，参数2：每页显示条数） [cite: 362, 363]
        Page<User> pageParam = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询（这里传 null 代表查询全部用户，框架会自动拼接分页 SQL） [cite: 364, 366]
        Page<User> resultPage = userMapper.selectPage(pageParam, null);

        // 3. 返回结果（resultPage 中包含了 records 数据列表、total 总条数等） [cite: 367]
        return Result.success(resultPage);
    }
}