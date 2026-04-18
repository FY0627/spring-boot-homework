package com.school.homework.entity.vo;

import lombok.Data;

/**
 * 用户详情视图对象
 * 用于承接多表联查结果，并作为接口返回对象
 */
@Data
public class UserDetailVO {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String address;
}
