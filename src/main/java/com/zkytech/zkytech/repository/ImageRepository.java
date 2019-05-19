package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository  extends JpaRepository<Image,Long> {
    Image findImageById(Long id);
}
