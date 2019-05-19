package com.zkytech.zkytech.property;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zkytech.security")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityProperties {
    /**
     * rememberMe的有效时间，单位：秒。默认有效时间为一个月
     */
    private int rememberMeSeconds = 259200;
    private String uploadPath;

}
