package com.zkytech.zkytech.service;

import com.alibaba.fastjson.JSONObject;
import com.zkytech.zkytech.Utils;
import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author : Zhang Kunyuan
* @date: 2019/4/2 0002 14:33
* @description: 认证成功的处理器
*/
@Component
public class MyAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        MyApiResponse apiResponse=new MyApiResponse<>();
        User user = (User) authentication.getPrincipal();
        apiResponse.setData(user);
        apiResponse.setSuccess(true);
        apiResponse.setMessage("登录成功！");
        response.getWriter().write(JSONObject.toJSONString(apiResponse));
        logger.info("用户"+authentication.getName()+"成功通过登录认证！");
    }
}
