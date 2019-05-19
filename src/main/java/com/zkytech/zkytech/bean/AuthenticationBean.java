package com.zkytech.zkytech.bean;

import lombok.Data;

/**
* @author : Zhang Kunyuan
* @date: 2019/5/5 0005 19:04
* @description: 登录时需要验证的内容
*/
@Data
public class AuthenticationBean {
    private String username;
    private String password;
}
