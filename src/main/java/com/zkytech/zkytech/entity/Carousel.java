package com.zkytech.zkytech.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zkytech.zkytech.Utils;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* @author : Zhang Kunyuan
* @date: 2019/5/6 0006 21:11
* @description: 首页轮播内容
*/
@Data
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class) //配合@CreateDate、@LastModifiedDate
@DynamicInsert //在插入时，如果传入的字段值为null，则使用default value
@DynamicUpdate //在更新时，如果传入的字段值为null，则不对该字段进行操作
public class Carousel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @NonNull @NotEmpty(message = "图片链接不能为空") @Column(nullable = false)
     private String imgUrl;

    @NonNull @NotNull(message = "文章Id不能为空") @Column(nullable = false)
     private Long articleId;

    @Column(nullable = false, columnDefinition = "varchar(50) default ''")
     private String title; //显示在轮播图之上的文字

    @Column(nullable = false, columnDefinition = "int(2) default 0",name = "carousel_rank",insertable = false)
     private int rank; //排序序号，用于确定轮播顺序

    @Column(nullable = false,columnDefinition = "tinyint(1) default 0",insertable = false)
     private boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @CreatedDate
    @Column(nullable = false)
    //生成时间
    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @LastModifiedDate
    @Column(nullable = false)
    //最后更新时间
    private Date lastModifiedDate;

    /**
     * 在json序列化时会自动解析该getter并返回对应的内容
     * 前提是 方法本身必须是public
     * @return
     */

    public String getArticleTitle(){
        Utils utils = new Utils();
        return utils.findArticleTitleById(this.articleId);
    }


}
