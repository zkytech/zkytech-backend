package com.zkytech.zkytech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkytech.zkytech.bean.AuthenticationBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
* @author : Zhang Kunyuan
* @date: 2019/5/5 0005 19:10
* @description: 处理登录验证过程的filter
*/
public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //attempt Authentication when Content-Type is json
        if (request.getMethod().equals("POST")) {
            if(request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    ||request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){

                //use jackson to deserialize json
                ObjectMapper mapper = new ObjectMapper();
                UsernamePasswordAuthenticationToken authRequest = null;
                try (InputStream is = request.getInputStream()){
                    AuthenticationBean authenticationBean = mapper.readValue(is,AuthenticationBean.class);
                    authRequest = new UsernamePasswordAuthenticationToken(
                            authenticationBean.getUsername(), authenticationBean.getPassword());
                }catch (IOException e) {
                    e.printStackTrace();
                    authRequest = new UsernamePasswordAuthenticationToken(
                            "", "");
                }finally {
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                }
            }else {
                return super.attemptAuthentication(request, response);
            }
        }else{
            return super.attemptAuthentication(request, response);
        }


        //transmit it to UsernamePasswordAuthenticationFilter


    }
}
