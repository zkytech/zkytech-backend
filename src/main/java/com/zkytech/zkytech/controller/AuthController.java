package com.zkytech.zkytech.controller;

import com.google.code.kaptcha.Producer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zkytech.zkytech.Utils;
import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.bean.ImgCode;
import com.zkytech.zkytech.bean.UserType;
import com.zkytech.zkytech.bean.params.SignupParams;
import com.zkytech.zkytech.entity.MailVerifyCode;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.repository.*;
import com.zkytech.zkytech.service.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.*;

@Controller
@RequestMapping("auth")
public class AuthController {

    private final ArticleRepository articleRepository;
    private final ClassificationRepository classificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CarouselRepository carouselRepository;
    private final Utils utils = new Utils();
    private static final String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";
    private final Producer captchaProducer;
    private final MailService mailService;
    private final MailVerifyCodeRepository mailVerifyCodeRepository;
    /**
     *  创建线程池
     */
    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("zkytech-pool-%d").build();
    ExecutorService pool = new ThreadPoolExecutor(5, 200, 0L,
            TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(1024),namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());


    @Autowired
    public AuthController(ArticleRepository articleRepository, ClassificationRepository classificationRepository,
                          PasswordEncoder passwordEncoder, UserRepository userRepository,
                          CarouselRepository carouselRepository, Producer captchaProducer,
                          MailService mailService, MailVerifyCodeRepository mailVerifyCodeRepository) {
        this.articleRepository = articleRepository;
        this.classificationRepository = classificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.carouselRepository = carouselRepository;
        this.captchaProducer = captchaProducer;
        this.mailService = mailService;
        this.mailVerifyCodeRepository = mailVerifyCodeRepository;
    }

    /**
     * springSecurity的默认登录页指向了这里
     *
     * @return : {"success":false,"message":"请在登录后访问该资源"}
     * @author : Zhang Kunyuan
     * @date : 2019/5/5 0005 19:08
     */

    @GetMapping("loginRequired")
    @ResponseBody
    public MyApiResponse loginRequired() {
        MyApiResponse apiResponse = new MyApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("请在登录后访问该资源");
        return apiResponse;
    }

    @PostMapping("signup")
    @ResponseBody
    public MyApiResponse signup(@RequestBody SignupParams params, HttpSession session) {
        MyApiResponse apiResponse = new MyApiResponse();
        if (session.getAttribute(SESSION_KEY_IMAGE_CODE) == null) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("请刷新验证码后重试");
            return apiResponse;
        }
        ImgCode imgCode = (ImgCode) session.getAttribute(SESSION_KEY_IMAGE_CODE);
        LocalDateTime localDateTime = LocalDateTime.now();
        if (localDateTime.isBefore(imgCode.getExpireTime()) && params.getCode().equals(imgCode.getCode())) {
            // 需要注册管理员账户时对UserType进行修改即可
            User user = User.of(params.getUsername(), passwordEncoder.encode(params.getPassword()), UserType.DEFAULT, params.getEmail());
            userRepository.save(user);
            apiResponse.setMessage("注册成功！");
            // 发送验证邮件
            Long userId = userRepository.findUserByUsername(params.getUsername()).getId();

            // 这在并发量大时是不行的
            pool.execute(()-> mailService.sendHtmlMail(params.getEmail(), "zkyTech-验证邮件", utils.getMailContent(userId, params.getUsername())));
        } else {
            apiResponse.setSuccess(false);
            if (!localDateTime.isBefore(imgCode.getExpireTime())) {
                apiResponse.setMessage("验证码已过期，请刷新验证码后重试！");
            } else {
                apiResponse.setMessage("验证码错误，请刷新验证码后重试");
            }
        }


        session.removeAttribute(SESSION_KEY_IMAGE_CODE);
        return apiResponse;
    }



    /**
     * 获取验证码
     *
     * @param response
     * @param request
     * @param session
     * @throws IOException
     */
    @GetMapping("code")
    public void getImgCode(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {
        // 60秒过期的验证码
        ImgCode imgCode = utils.getImgCode();
        session.setAttribute(SESSION_KEY_IMAGE_CODE, imgCode);
        ImageIO.write(imgCode.getImage(), "JPEG", response.getOutputStream());
        response.setContentType("image/jpeg");
    }

    /**
     * 用户名、邮箱重复性校验
     *
     * @param username
     * @param email
     * @return
     */
    @GetMapping("check")
    @ResponseBody
    public MyApiResponse checkSignupInfo(@RequestParam(required = false, defaultValue = "") String username, @RequestParam(required = false, defaultValue = "") String email) {
        MyApiResponse apiResponse = new MyApiResponse();
        User user = null;
        if (!username.isEmpty()) {
            user = userRepository.findUserByUsername(username);
            if(user == null){
                user = userRepository.findUserByEmail(username);
            }

            if (user != null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("该用户名已被注册");
            } else {
                apiResponse.setMessage("该用户名可以注册");
            }
        }
        if (!email.isEmpty()) {
            user = userRepository.findUserByEmail(email);
            if (user != null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("该邮箱已被注册");
            } else {
                apiResponse.setMessage("");
            }
        }
        return apiResponse;
    }

    @GetMapping("checkRememberMe")
    @ResponseBody
    public MyApiResponse checkRememberMe(Authentication authentication) {
        MyApiResponse apiResponse = new MyApiResponse();

        if(authentication!=null){
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username);
            if(user == null){
                authentication.setAuthenticated(false);
                System.out.println(username+"身份认证已过期");
                apiResponse.setSuccess(false);
                apiResponse.setMessage("身份认证已过期");
            }else{
                apiResponse.setData(user);
                apiResponse.setMessage("用户处于登录状态");
            }

        }else{
            apiResponse.setSuccess(false);
            apiResponse.setMessage("用户未登录");
        }
        return apiResponse;
    }

    @GetMapping("mailCheck")
    @ResponseBody
    public String checkMailCode(@RequestParam String code) {
        MailVerifyCode mailVerifyCode = mailVerifyCodeRepository.findMailVerifyCodeByCode(code);
        if(mailVerifyCode != null){
            User user = userRepository.findUserById(mailVerifyCode.getUserId());
            user.setVerified(true);
            userRepository.save(user);
            mailVerifyCodeRepository.delete(mailVerifyCode);
            System.out.println("用户"+user.getUsername()+"认证成功");
            return "认证成功";
        }else{
            return "请检查链接是否完整";
        }
    }

    /**
     * 重新发送验证邮件
     * @param authentication
     * @return
     */
    @PostMapping("sendEmail")
    @ResponseBody
    public MyApiResponse sendEmail(Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();
        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        String content = utils.getMailContent(user.getId(), user.getUsername());
        pool.execute(()-> mailService.sendHtmlMail(email, "zkyTech-验证邮件",content ));
        apiResponse.setMessage("验证邮件已发送");
        return apiResponse;
    }

}
