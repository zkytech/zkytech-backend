package com.zkytech.zkytech.bean;


import com.zkytech.zkytech.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ImgCode {

    // 验证码文本
    private String code;

    // 验证码图片
    private BufferedImage image;

    // 过期时间
    private LocalDateTime expireTime;

    public ImgCode(BufferedImage image, String code, int expireIn){
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

}
