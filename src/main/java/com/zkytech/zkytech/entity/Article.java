package com.zkytech.zkytech.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
* @date: 2019/5/5 0005 19:03
* @description: 文章
*/
@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class) //配合@CreateDate、@LastModifiedDate
@DynamicInsert //在插入时，如果传入的字段值为null，则使用default value
@DynamicUpdate //在更新时，如果传入的字段值为null，则不对该字段进行操作
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") //当进行双向关联时，需要进行这项处理以避免无限递归
public class Article {
    @Id @Column(nullable = false) @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull @Column(nullable = false)
    @NotEmpty(message = "标题不能为空")
    //文章标题
    private String title;

    @Column(columnDefinition = "longtext", nullable = false)
    @NotEmpty(message = "文章内容不能为空")
    //文章整体
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @CreatedDate @Column(nullable = false)
    //生成时间
    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @LastModifiedDate @Column(nullable = false)
    //最后更新时间
    private Date lastModifiedDate;

    @Column(nullable = false, columnDefinition = "int default 0")  @NotNull
    //点击量
    private int clicks;

    @NonNull
    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH}, optional=false)   //optional=false表示classification不能为空， 删除操作不会影响到classification表
    @JoinColumn(name="classification_id") // 外键
    private Classification classification;

    public Article(@NonNull @NotEmpty(message = "标题不能为空") String title, @NotEmpty(message = "文章内容不能为空") String content, @NonNull Classification classification) {
        this.title = title;
        this.content = content;
        this.classification = classification;
    }

    public Article(Long id,@NonNull @NotEmpty(message = "标题不能为空") String title, @NotNull int clicks, @NonNull Classification classification) {
        this.title = title;
        this.clicks = clicks;
        this.classification = classification;
        this.id = id;
    }

//    public String getCreatedDate() {
//        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdDate);
////        return DateFormat.getDateInstance(DateFormat.FULL).format(date);
//    }

//    public String getLastModifiedDate() {
//        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastModifiedDate);
//    }
}
