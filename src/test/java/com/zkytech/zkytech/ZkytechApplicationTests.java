package com.zkytech.zkytech;

import com.zkytech.zkytech.repository.CarouselRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZkytechApplicationTests {
    @Autowired
    CarouselRepository carouselRepository;
    @Test
    public void contextLoads() {

    }

}
