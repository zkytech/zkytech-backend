package com.zkytech.zkytech.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zkytech.zkytech.Utils;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private long id;

    @Column(nullable = false)
    @NonNull @NotNull(message = "文章Id不能为空")
    private Long articleId;


    @Column(nullable = false)
    @NonNull @NotNull(message = "用户ID不能为空")
    private Long userId;


    @Column(nullable = false)
    @NonNull @NotNull(message = "目标用户的Id不能为空")
    private Long targetUserId;

    /**
     * 评论内容
     */
    @Column(columnDefinition = "longtext", nullable = false) 
    @NonNull @NotEmpty(message = "评论内容不能为空")
    private String content;


    @Column(nullable = false) 
    @NonNull @NotNull(message = "楼层数不能为空")
    private int floor;

    /**
     * -1表示是直属于文章，其它数字表示属于文章下某一评论的二级评论的楼层数
     */
    @NonNull @NotNull(message = "楼层位置不能为空") 
    private int insideFloor;

    /**
     * 评论目标ID
     */
    @Column(nullable = false)
    @NonNull @NotNull(message = "目标Id不能为空")
    private long targetId;

    @NonNull @NotNull(message = "目标楼层数不能为空") 
    private int targetFloor;

    /**
     * 支持数
     * 仅凭这个数据不能保证支持与反对的真实性，因此还要另外设置一张表做用户点支持与反对的记录。
     */
    @Column (columnDefinition = "int(10) default 0") 
    private int upvote;

    /**
     * 反对数
     */
    @Column (columnDefinition = "int(10) default 0") 
    private int downvote;

    /**
     * 评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @CreatedDate
    @Column(nullable = false) 
    private Date createdDate;

    /**
     * 评论是否已被评论对象阅读
     */
    @Column(columnDefinition = "boolean default false",name = "comment_read")
    private boolean read;

    public String getAvatar(){
        Utils utils = new Utils();
        return utils.getAvatarByUserId(this.userId);
    }


    public String getAuthor(){
        Utils utils = new Utils();
        return utils.getUsernameByUserId(this.userId);
    }

    public String getTargetUsername(){
        Utils utils = new Utils();
        return utils.getUsernameByUserId(targetUserId);
    }

    public String getArticleTitle(){
        Utils utils = new Utils();
        return utils.findArticleTitleById(articleId);
    }


}
