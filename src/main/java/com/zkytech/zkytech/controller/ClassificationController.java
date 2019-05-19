package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.entity.Classification;
import com.zkytech.zkytech.repository.ClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("classification")
public class ClassificationController {


    private final ClassificationRepository classificationRepository;


    @Autowired
    public ClassificationController(  ClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
    }

    /**
     * 返回分类列表
     *
     * @return :{@link MyApiResponse}
     * @author : Zhang Kunyuan
     * @date : 2019/5/5 0005 21:13
     */
    @GetMapping("/list")
    public MyApiResponse getClassificationList(){
        List<Classification> classificationsList =  classificationRepository.findAll();
        MyApiResponse<List> apiResponse = new MyApiResponse<>();
        apiResponse.setData(classificationsList);
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    /**
     * 添加classification
     * @param classification :{"classificationName":"classificationName"}
     * @return : {@link MyApiResponse}
     */
    @PutMapping
    public MyApiResponse addClassification(@RequestBody Classification classification){
        MyApiResponse apiResponse = new MyApiResponse();
        classificationRepository.save(classification);
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    /**
     * 修改classification
     * @param classification :{"classificationName":"classificationName","id":"id"}
     * @return
     */
    @PostMapping
    public MyApiResponse editClassification(@RequestBody Classification classification){
        MyApiResponse apiResponse = new MyApiResponse();
//        Classification classification = classificationRepository.findClassificationById(Long.valueOf(params.get("id").toString()));
//        classification.setClassificationName(classification.getClassificationName());
        classificationRepository.save(classification);
        return apiResponse;
    }

    /**
     * 删除classification
     * @param id :{
     * @return
     */
    @DeleteMapping("/{id}")
    public MyApiResponse deleteClassification(@PathVariable Long id){
        MyApiResponse apiResponse = new MyApiResponse();
        classificationRepository.deleteById(id);
        apiResponse.setSuccess(true);
        return apiResponse;
    }
}
