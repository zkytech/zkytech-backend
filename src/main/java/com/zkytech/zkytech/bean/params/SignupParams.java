package com.zkytech.zkytech.bean.params;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SignupParams {
    private String username;
    private String password;
    private String code;
    private String email;
}
