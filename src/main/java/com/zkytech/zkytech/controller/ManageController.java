package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.listener.MySessionListener;
import com.zkytech.zkytech.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("manage")
public class ManageController {

   private final ArticleRepository articleRepository;

    public ManageController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 在线人数统计
     * @return
     */

    @GetMapping("/online")
    public MyApiResponse getOnlineCount(){
        MyApiResponse apiResponse = new MyApiResponse();
        apiResponse.setData(MySessionListener.online);
        return apiResponse;
    }

    @GetMapping("/clicks")
    public MyApiResponse getClicksCount(){
        MyApiResponse apiResponse = new MyApiResponse();
        int clicksCount = articleRepository.getTotalClicks();
        apiResponse.setData(clicksCount);
        return apiResponse;
    }

}
