package com.zkytech.zkytech.service;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


// Spring Security获取用户信息的组件

/**
* @author : Zhang Kunyuan
* @date: 2019/4/2 0002 14:01
* @description: 自定义Spring Security获取用户数据的方法
*/
@Component("myUserDetailServiceImpl")
public class MyUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    public MyUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if(user == null){
            // 用户名、邮箱混合登录
            user = this.userRepository.findUserByEmail(username);
        }
        logger.info("登录用户名："+username);
        if(user!=null){
            // 虽然这里做的只是返回一个用户信息，但是Spring Security会自动对密码进行校验
            // 这里返回的user会保存到authentication中：(User) authentication.getPrincipal();
            return user;
        }else{
         throw new UsernameNotFoundException("未找到用户："+username);
        }
    }
}
