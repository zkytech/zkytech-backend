package com.zkytech.zkytech.service;

import com.alibaba.fastjson.JSONObject;
import com.zkytech.zkytech.bean.MyApiResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author : Zhang Kunyuan
* @date: 2019/4/2 0002 14:33
* @description: 认证失败的处理器
*/
@Component
public class MyAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        MyApiResponse apiResponse=new MyApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("登录验证失败");
        response.getWriter().write(JSONObject.toJSONString(apiResponse));
    }
}
