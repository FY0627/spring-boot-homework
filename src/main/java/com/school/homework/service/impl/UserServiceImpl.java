package com.school.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.homework.common.Result;
import com.school.homework.common.ResultCode;
import com.school.homework.entity.User;
import com.school.homework.entity.dto.UserDTO;
import com.school.homework.entity.vo.UserDetailVO;
import com.school.homework.mapper.UserMapper;
import com.school.homework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.homework.entity.UserInfo;
import com.school.homework.mapper.UserInfoMapper;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final String CACHE_KEY_PREFIX = "user:detail:";

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
        // ... (省略原有逻辑，保持文件结构)
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> resultPage = userMapper.selectPage(pageParam, null);
        return Result.success(resultPage);
    }

    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;

        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isBlank()) {
            try {
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据解析异常，删掉脏缓存，继续查询数据库
                redisTemplate.delete(key);
            }
        }

        // 2. 查数据库 (多表联查)
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 写缓存 (设置 10 分钟过期)
        redisTemplate.opsForValue().set(
                key,
                JSONUtil.toJsonStr(detail),
                10,
                TimeUnit.MINUTES
        );

        return Result.success(detail);
    }

        @Override
    @Transactional
    public Result<String> updateUserInfo(UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.error(ResultCode.ERROR);
        }

        // 1. 先操作 DB：通过 userId 作为条件进行更新，而不是主键 id
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserInfo> updateWrapper = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfo::getUserId, userInfo.getUserId());
        
        // 执行更新
        int rows = userInfoMapper.update(userInfo, updateWrapper);
        
        // 如果更新失败（可能该用户在 user_info 表里还没记录），则执行插入
        if (rows == 0) {
            userInfoMapper.insert(userInfo);
        }

        // 2. 成功后删除旧缓存 (Cache-Aside 策略)
        String key = CACHE_KEY_PREFIX + userInfo.getUserId();
        redisTemplate.delete(key);

        return Result.success("更新成功");
    }


    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        // 1. 先操作 DB
        int rows = userMapper.deleteById(userId);
        if (rows <= 0) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 成功后删除旧缓存
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(key);

        return Result.success("删除成功");
    }
}