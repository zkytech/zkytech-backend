package com.zkytech.zkytech.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@ToString
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户Id
     */
    @NonNull @NotNull(message = "用户Id不能为空")
    private Long userId;

    /**
     * 评论Id
     */
    @NonNull @NotNull(message = "评论Id不能为空")
    private Long commentId;

    /**
     * 区分支持与反对，支持为true，反对为false
     */
    @NonNull @NotNull(message = "类型不能为空")
    private boolean upvote;
}
