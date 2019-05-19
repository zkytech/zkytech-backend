package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.listener.MySessionListener;
import com.zkytech.zkytech.repository.ArticleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


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
        Optional<Integer> clicksCount = articleRepository.getTotalClicks();
        if(!clicksCount.isPresent()){
            clicksCount = Optional.of(0);
        }
        apiResponse.setData(clicksCount.get());
        return apiResponse;
    }
}
