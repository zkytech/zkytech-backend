package com.zkytech.zkytech;

import com.google.code.kaptcha.Producer;
import com.zkytech.zkytech.bean.ImgCode;
import com.zkytech.zkytech.entity.Classification;
import com.zkytech.zkytech.entity.MailVerifyCode;
import com.zkytech.zkytech.entity.Session;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.property.SecurityProperties;
import com.zkytech.zkytech.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;


@Component
public class Utils {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CarouselRepository carouselRepository;
    @Autowired
    ClassificationRepository classificationRepository;
    @Autowired
    Producer captchaProducer;
    @Autowired
    SecurityProperties securityProperties;
    @Autowired
    MailVerifyCodeRepository mailVerifyCodeRepository;
    @Autowired
    SessionRepository sessionRepository;


    public static Utils utils;

    @PostConstruct
    public void init() {
        utils = this;
        utils.userRepository = this.userRepository;
        utils.articleRepository = this.articleRepository;
        utils.carouselRepository = this.carouselRepository;
        utils.classificationRepository = this.classificationRepository;
        utils.captchaProducer = this.captchaProducer;
        utils.securityProperties = this.securityProperties;
        utils.mailVerifyCodeRepository = this.mailVerifyCodeRepository;
    }

    public Classification findClassificationById(Long id){
        return utils.classificationRepository.findClassificationById(id);
    }

    public User findUserByUsername(String username){
        return utils.userRepository.findUserByUsername(username);
    }


    public ImgCode getImgCode(){
        String text = utils.captchaProducer.createText();
        BufferedImage image = utils.captchaProducer.createImage(text);
        return new ImgCode(image,
                text,
                utils.securityProperties.getRememberMeSeconds());
    }


    public static String getRandomChar(int length) {            //生成随机字符串
        char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(chr[random.nextInt(36)]);
        }
        return buffer.toString();
    }

    public String getMailContent(Long userId, String username){
        String randChars = GetUUID();
        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" dir=\"ltr\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title></title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div>\n" +
                String.format("      你好，%s<br>\n", username) +
                String.format("      请点击<a href=\"https://blog.zkytech.top/api/auth/mailCheck?code=%s\">链接</a>进行验证<br>\n", randChars) +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";
        MailVerifyCode mailVerifyCode = new MailVerifyCode(randChars,userId);
        utils.mailVerifyCodeRepository.save(mailVerifyCode);
        return content;
    }

    public static String GetUUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public String getAvatarByUserId(Long userId){
        return utils.userRepository.findAvatarById(userId);
    }

    public String getUsernameByUserId(Long userId){
        return utils.userRepository.findUsernameById(userId);
    }

    public String findArticleTitleById(Long id){return utils.articleRepository.findArticleTitleById(id);}

    public static String getIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static void saveSession(Session session){
        utils.sessionRepository.save(session);
    }

}
