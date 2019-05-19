package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
/**
* @author : Zhang Kunyuan
* @date: 2019/5/5 0005 19:57
* @description: 文章类型的Repository
*/
public interface ClassificationRepository extends JpaRepository<Classification,Long> {
    Classification findClassificationById(Long id);
}
