package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.entity.Carousel;
import com.zkytech.zkytech.repository.CarouselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("carousel")
public class CarouselController {
    private final CarouselRepository carouselRepository;

    @Autowired
    public CarouselController( CarouselRepository carouselRepository) {

        this.carouselRepository = carouselRepository;
    }

    /**
     * 根据激活状态查询走马灯信息
     * @param pageSize
     * @param page
     * @param active: true/false
     * @return {@link MyApiResponse}
     */
    @GetMapping("/list")
    public MyApiResponse getCarouselByState(@RequestParam(required = false, defaultValue = "-1") int pageSize, @RequestParam(required = false, defaultValue = "-1") int page, @RequestParam(required = false, defaultValue = "") String active){
        MyApiResponse apiResponse = new MyApiResponse();
        List<Sort.Order> orders=new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC,"lastModifiedDate" ));
        Page<Carousel> carouselPage = null;
        List<Carousel> carouselList = null;
        if(pageSize != -1 && page !=-1){
            if(!active.isEmpty()){
                Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(orders));
                carouselPage = carouselRepository.findCarouselsByActive(active.equalsIgnoreCase("true") , pageable);
            }else{
                orders.add(new Sort.Order(Sort.Direction.DESC,"active"));
                Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(orders));
                carouselPage = carouselRepository.findAll(pageable);
            }
            apiResponse.setData(carouselPage);
        }else{
            if(!active.isEmpty()){
                carouselList = carouselRepository.findCarouselsByActive(active.equalsIgnoreCase("true"),Sort.by(orders));
            }else{
                carouselList = carouselRepository.findAll(Sort.by(orders));
            }
            apiResponse.setData(carouselList);
        }
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    /**
     * 添加走马灯信息
     * @param carousel : {"id":{@link Long}, "imgUrl":{@link String},"articleId":{@link Long},"rank":{@link Integer},"active":{@link Boolean}}
     * @return {@link MyApiResponse}
     */
    @PutMapping()
    public MyApiResponse addCarousel(@RequestBody Carousel carousel){
        MyApiResponse apiResponse = new MyApiResponse();
        carouselRepository.save(carousel);
        apiResponse.setSuccess(true);
        return apiResponse;
    }


    /**
     * 编辑走马灯信息
     * @param carousel :{"id":{@link Long}, "imgUrl":{@link String},"articleId":{@link Long},"rank":{@link Integer},"active":{@link Boolean}}
     * @return :{@link MyApiResponse}
     */
    @PostMapping()
    public MyApiResponse editCarousel(@RequestBody Carousel carousel){
        MyApiResponse apiResponse = new MyApiResponse();
        carouselRepository.save(carousel);
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    /**
     * 删除走马灯信息
     * @param id :
     * @return {@link MyApiResponse}
     */
    @DeleteMapping("/{id}")
    public MyApiResponse deleteCarousel(@PathVariable Long id){
        MyApiResponse apiResponse = new MyApiResponse();
        carouselRepository.deleteById(id);
        apiResponse.setMessage("删除成功");
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    @PostMapping("/list")
    public MyApiResponse editCarousels(@RequestBody List<Carousel> carouselList){
        MyApiResponse apiResponse = new MyApiResponse();
        Iterator<Carousel> iter = carouselList.iterator();
        while(iter.hasNext()){
            Carousel carousel = iter.next();
            carouselRepository.saveAndFlush(carousel);
        }
        apiResponse.setSuccess(true);
        apiResponse.setMessage("修改成功");
        return apiResponse;
    }

}
