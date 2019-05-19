package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Carousel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarouselRepository extends JpaRepository<Carousel, Long> {
    Page<Carousel> findCarouselsByActive(boolean active, Pageable pageable);
    List<Carousel> findCarouselsByActive(boolean active,Sort sort);
    Carousel findCarouselById(Long id);
}

