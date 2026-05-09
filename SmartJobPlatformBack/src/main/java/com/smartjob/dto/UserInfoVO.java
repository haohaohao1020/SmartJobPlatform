package com.smartjob.dto;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private String role;
    private Integer gender;
    private String birthDate;
    private Integer workYears;
    private String highestEducation;
    private String currentCity;
    private Integer phoneStatus;
    private Integer emailStatus;
    private Long companyId;
    private String companyName;
    private String position;
    private String username;
    private String lastLoginTime;
    private String createTime;
    private Integer status;
}
