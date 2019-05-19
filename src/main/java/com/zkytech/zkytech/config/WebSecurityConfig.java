package com.zkytech.zkytech.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.zkytech.zkytech.bean.UserType;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.property.SecurityProperties;
import com.zkytech.zkytech.service.MyAuthenticationFilter;
import com.zkytech.zkytech.service.MyLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Properties;


/**
* @author : Zhang Kunyuan
* @date: 2019/4/2 0002 14:09
* @description: Web权限认证相关的配置
*/
@EnableConfigurationProperties(SecurityProperties.class)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final SecurityProperties securityProperties;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;
    private final MyLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    public WebSecurityConfig(SecurityProperties securityProperties, AuthenticationSuccessHandler myAuthenticationSuccessHandler, AuthenticationFailureHandler myAuthenticationFailureHandler, DataSource dataSource, @Qualifier("myUserDetailServiceImpl") UserDetailsService userDetailsService, MyLogoutSuccessHandler logoutSuccessHandler) {
        this.securityProperties = securityProperties;
        this.authenticationSuccessHandler = myAuthenticationSuccessHandler;
        this.authenticationFailureHandler = myAuthenticationFailureHandler;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 配置密码加密方式
        return new BCryptPasswordEncoder();
    }


    /**
     * 实现“记住我”功能的Repository
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 程序启动时自动创建表
        // 首次运行后需要将这行注释掉
//        tokenRepository.setCreateTableOnStartup(true);

        return tokenRepository;
    }


    /**
     * 验证码生成工具
     * @return
     */
    @Bean
    Producer captchaProducer(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "110");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/api/auth/loginRequired")
                .loginProcessingUrl("/api/auth/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
            .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .and()
            .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(securityProperties.getRememberMeSeconds())
                .userDetailsService(userDetailsService)
                .rememberMeParameter("rememberMe").alwaysRemember(false)
                .and()
            .authorizeRequests()
                // 必须添加这一配置，否则将会在进入登录页时出现循环重定向
                // 权限认证示例
                .antMatchers(HttpMethod.GET,"/api/auth/checkRememberMe","/api/user/**").permitAll()
                .antMatchers(HttpMethod.POST,"/api/auth/**","/api/article/click/**").permitAll()
                .antMatchers(HttpMethod.PUT,"/api/comment").hasAnyAuthority(UserType.DEFAULT,UserType.ADMIN)
                .antMatchers(HttpMethod.POST, "/api/upload/**","/api/comment","/api/comment/readMessage","/api/user").hasAnyAuthority(UserType.ADMIN,UserType.DEFAULT)
                .antMatchers(HttpMethod.GET,"/api/image/**","/api/manage/**","/api/user/list").hasAnyAuthority(UserType.ADMIN)
                .antMatchers(HttpMethod.DELETE).hasAnyAuthority(UserType.ADMIN)
                .antMatchers(HttpMethod.POST).hasAnyAuthority(UserType.ADMIN)
                .antMatchers(HttpMethod.PUT).hasAnyAuthority(UserType.ADMIN)
                .antMatchers("/api/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                // 禁用csrf
            .csrf().
                disable();
        //用重写的Filter替换掉原有的UsernamePasswordAuthenticationFilter
//        http.addFilterAt(myAuthenticationFilter(),
//                UsernamePasswordAuthenticationFilter.class);
    }

    //注册自定义的UsernamePasswordAuthenticationFilter
//    @Bean
//    MyAuthenticationFilter myAuthenticationFilter() throws Exception {
//        MyAuthenticationFilter filter = new MyAuthenticationFilter();
//        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
//        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
//        filter.setFilterProcessesUrl("/auth/login");
//        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
//        filter.setAuthenticationManager(authenticationManagerBean());
//        return filter;
//    }


}
