package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.entity.Image;
import com.zkytech.zkytech.property.SecurityProperties;
import com.zkytech.zkytech.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("image")
public class ImageController {
    private final ImageRepository imageRepository;
    private final SecurityProperties securityProperties;
    public ImageController(ImageRepository imageRepository, SecurityProperties securityProperties) {
        this.imageRepository = imageRepository;
        this.securityProperties = securityProperties;
    }

    @GetMapping("/list")
    public MyApiResponse<List<Image>> getImageList(){
        MyApiResponse<List<Image>> apiResponse = new MyApiResponse<>();
        List<Image> imageList = imageRepository.findAll();
        apiResponse.setData(imageList);
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public MyApiResponse deleteImage(@PathVariable Long id){
        MyApiResponse apiResponse = new MyApiResponse();
        Image image = imageRepository.findImageById(id);
        // 不止是从数据库删除，还要删除磁盘中的文件

        File file = new File(securityProperties.getUploadPath()+image.getImgUrl());
        if(file.delete()){
            apiResponse.setMessage("删除成功");
            imageRepository.delete(image);
        }else{
            apiResponse.setMessage("删除失败");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }
}
