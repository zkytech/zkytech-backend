package com.zkytech.zkytech.bean;


import lombok.*;

/**
* @author : Zhang Kunyuan
* @date: 2019/5/5 0005 21:18
* @description: api返回模板
*/

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MyApiResponse<T> {
        @Getter @Setter private boolean success = true;
        @Getter @Setter private T data;
        @Getter @Setter String message;
}
