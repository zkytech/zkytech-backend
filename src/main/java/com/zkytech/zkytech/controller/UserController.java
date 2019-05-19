package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.Utils;
import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.bean.UserType;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.property.SecurityProperties;
import com.zkytech.zkytech.repository.ArticleRepository;
import com.zkytech.zkytech.repository.CarouselRepository;
import com.zkytech.zkytech.repository.ClassificationRepository;
import com.zkytech.zkytech.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    private final ArticleRepository articleRepository;
    private final ClassificationRepository classificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CarouselRepository carouselRepository;
    private final SecurityProperties securityProperties;
    private final PersistentTokenRepository persistentTokenRepository;
    private final Utils utils = new Utils();
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public UserController(ArticleRepository articleRepository, ClassificationRepository classificationRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, CarouselRepository carouselRepository, SecurityProperties securityProperties, PersistentTokenRepository persistentTokenRepository) {
        this.articleRepository = articleRepository;
        this.classificationRepository = classificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.carouselRepository = carouselRepository;
        this.securityProperties = securityProperties;
        this.persistentTokenRepository = persistentTokenRepository;
    }

    /**
     * 修改用户信息
     * @param newUsername
     * @param email
     * @param authentication
     * @return
     */
    @PostMapping()
    public MyApiResponse editUserInfo(@RequestParam(required = false, defaultValue = "",name = "username") String newUsername, @RequestParam(required = false, defaultValue = "") String email, Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();
        if(authentication==null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("非法请求");
            logger.warn("非法修改用户信息请求");
            return apiResponse;
        }
        String username = authentication.getName();

        if (!newUsername.isEmpty()) {

            // 由于允许使用邮箱登录，所以还要额外校验是否存在同名邮箱
            User user = userRepository.findUserByUsername(newUsername);
            if(user == null){
                user = userRepository.findUserByEmail(newUsername);
            }

            if(user!=null){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("该用户名已被注册");
                return apiResponse;
            }
            user = userRepository.findUserByUsername(username);
            user.setUsername( newUsername );
            userRepository.save(user);
            apiResponse.setMessage("用户名修改成功");
            // 删除记住我功能中的持久化信息
            persistentTokenRepository.removeUserTokens(username);
        }
        
        if(!email.isEmpty()){
            // 邮箱修改逻辑较为复杂，暂不实现
            
        }

        return apiResponse;
    }

    /**
     * 获取用户信息（在json序列化时会自动过滤掉密码）
     * @param id
     * @param authentication
     * @return
     */
    @GetMapping("/{id}")
    public MyApiResponse getUserInfo(@PathVariable Long id, Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();
        User user = userRepository.findUserById(id);
        User user1 = null;
        // 获取security认证用户的信息
        if(authentication != null){
            user1 = (User) authentication.getPrincipal();
        }

        if(user!=user1){
            // 如果不是本人将不能获取email
            user.setEmail("");
        }
        apiResponse.setData(user);
        return apiResponse;
    }

    @GetMapping("/list")
    public MyApiResponse getUserList(@RequestParam(required = false, defaultValue = "1") int page,@RequestParam(required = false, defaultValue = "10") int pageSize, Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();
        Sort sort = Sort.by(Sort.Direction.DESC,"createdDate");
        Pageable pageable = PageRequest.of(page-1,pageSize,sort);
        Page<User> userPage = userRepository.findAll(pageable);
        apiResponse.setData(userPage);
        return apiResponse;
    }

    @PostMapping("/{id}")
    public MyApiResponse disableUser(@PathVariable Long id,@RequestParam(required = true) boolean enabled, Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();

        if(authentication==null){
            apiResponse.setMessage("非法访问");
            apiResponse.setSuccess(false);
            return apiResponse;
        }
        User user = userRepository.findUserById(id);
        user.setEnabled(enabled);
        apiResponse.setMessage("用户状态修改成功");
        userRepository.save(user);
        return apiResponse;
    }
}
