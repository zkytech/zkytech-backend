package com.zkytech.zkytech.service;

import com.alibaba.fastjson.JSONObject;
import com.zkytech.zkytech.bean.MyApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        MyApiResponse apiResponse=new MyApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("注销成功！");
        response.getWriter().write(JSONObject.toJSONString(apiResponse));
        logger.info("用户"+authentication.getName()+"注销登录！");
    }
}
