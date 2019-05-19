package com.zkytech.zkytech.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false) @NonNull @NotNull(message = "创建时间不能为空")
    //session的创建时间
    private Date createdDate;

    @NonNull @NotEmpty(message = "ip地址不能为空")
    private String ip;

}
