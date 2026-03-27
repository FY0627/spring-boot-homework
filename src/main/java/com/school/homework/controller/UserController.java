package com.school.homework.controller;

import com.school.homework.common.Result;
import com.school.homework.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        // 将原本直接返回的String 包装入 Result.success()中
        String data = "查询成功,正在返回 ID为 " + id + " 的用户信息";
        return Result.success(data);
    }

    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        String data = "新增成功，接收到用户：" + user.getName() + "，年龄：" + user.getAge();
        return Result.success(data);
    }

    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        String data = "更新成功，ID" + id + "的用户已修改成：" + user.getName();
        return Result.success(data);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        String data = "删除成功，已移除 ID 为" + id + "的用户";
        return Result.success(data);
    }
}