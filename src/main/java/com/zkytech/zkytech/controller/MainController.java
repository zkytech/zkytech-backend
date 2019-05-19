package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.Utils;
import com.zkytech.zkytech.bean.params.UploadResponse;
import com.zkytech.zkytech.entity.Image;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.property.SecurityProperties;
import com.zkytech.zkytech.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
* @author : Zhang Kunyuan
* @date: 2019/5/3 0003 10:57
* @description:
*/
@Controller
public class MainController implements ErrorController {
    // 自定义404处理方式必须implements ErrorController
    private final ArticleRepository articleRepository;
    private final ClassificationRepository classificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CarouselRepository carouselRepository;
    private final SecurityProperties securityProperties;
    private final ImageRepository imageRepository;
    private final Utils utils = new Utils();

    @Autowired
    public MainController(ArticleRepository articleRepository, ClassificationRepository classificationRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, CarouselRepository carouselRepository, SecurityProperties securityProperties, ImageRepository imageRepository) {
        this.articleRepository = articleRepository;
        this.classificationRepository = classificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.carouselRepository = carouselRepository;
        this.securityProperties = securityProperties;
        this.imageRepository = imageRepository;
    }



    /**
     * 自定义404处理
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/error")
    public void errorPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.sendRedirect("/index.html");
    }

    @PostMapping("upload/{type}")
    @ResponseBody
    public UploadResponse springUpload(HttpServletRequest request, @PathVariable String type,@RequestParam(required = false, defaultValue = "") String description, Authentication authentication) throws IllegalStateException, IOException
    {
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
                request.getSession().getServletContext());
        UploadResponse uploadResponse = new UploadResponse();
        if(authentication==null){
            uploadResponse.setStatus("error");
            return uploadResponse;
        }
        //检查form中是否有enctype="multipart/form-data"
        if(multipartResolver.isMultipart(request))
        {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
            //获取multiRequest 中所有的文件名
            Iterator iter=multiRequest.getFileNames();

            while(iter.hasNext())
            {
                //一次遍历所有文件
                MultipartFile file=multiRequest.getFile(iter.next().toString());
                if(file!=null)
                {
                    String path = String.format("%s/static/%s/%s",securityProperties.getUploadPath(),type,file.getOriginalFilename());
                    //上传
                    File f = new File(path);
                    File fileParent = f.getParentFile();
                    if(!fileParent.exists()){
                        fileParent.mkdirs();
                    }
                    file.transferTo(f);
                    String url = String.format("/static/%s/%s", type,file.getOriginalFilename());
                    String name = file.getOriginalFilename();
                    uploadResponse.setStatus("done");
                    uploadResponse.setUrl(url);
                    uploadResponse.setName(name);
                    switch (type){
                        case "avatar":
                            User user = (User) authentication.getPrincipal();
                            user.setAvatar(url);
                            userRepository.save(user);
                            break;
                        case "carousel":
                            imageRepository.save(Image.of(url,description));
                            break;
                        default:

                    }
                }
            }
        }else{
            uploadResponse.setStatus("error");
        }
        return uploadResponse;
    }


    @Override
    public String getErrorPath() {
        return "/error";
    }
}
