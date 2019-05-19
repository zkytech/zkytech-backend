package com.zkytech.zkytech.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * @author : Zhang Kunyuan
 * @date: 2019/5/5 0005 19:54
 * @description: 文章分类类型，与article表进行级联
 */
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Classification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(nullable = false)
    private Long id;

    @NonNull @Column(nullable = false)
    @NotEmpty(message = "分类名不能为空")
    // 级联保存、更新、删除、刷新;延迟加载。当删除用户，会级联删除该用户的所有文章
    // 拥有mappedBy注解的实体类为关系被维护端
    // mappedBy注解的实体类为关系被维护端
    private String classificationName;

//    @Getter @Setter
//    @OneToMany(mappedBy = "classification", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Article> articleList;


}
