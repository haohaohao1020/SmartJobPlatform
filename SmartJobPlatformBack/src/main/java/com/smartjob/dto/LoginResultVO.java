package com.smartjob.dto;

import lombok.Data;

@Data
public class LoginResultVO {
    private String token;
    private UserInfoVO userInfo;
}
