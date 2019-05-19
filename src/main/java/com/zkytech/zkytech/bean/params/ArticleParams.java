package com.zkytech.zkytech.bean.params;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ArticleParams {
    private Long id;
    private Long classificationId;
    private String content;
    private String title;
}
