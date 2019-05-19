package com.zkytech.zkytech.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) //配合@CreateDate、@LastModifiedDate
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "longtext") @NonNull @NotEmpty(message = "图片URL不能为空")
    private String imgUrl;

    @Column(columnDefinition = "varchar(50)") @NonNull
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @CreatedDate
    @Column(nullable = false)
    //生成时间
    private Date createdDate;
}
