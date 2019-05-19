package com.zkytech.zkytech.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name="ZKYTECH_USERS")
@ToString
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) //配合@CreateDate、@LastModifiedDate
@Data
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
    @SequenceGenerator(sequenceName="user_seq", allocationSize = 1, name="CUST_SEQ")
    @Column(unique = true,nullable = false)
     private Long id;

    // 用户名唯一
    @Column(columnDefinition = "VARCHAR(200)", unique=true, nullable = false)
    @NotEmpty(message = "用户名不能为空")
    @NonNull  private String username;

    // 用户密码，禁止在转为json时携带该字段
    @Column(columnDefinition = "VARCHAR(200)",nullable = false)
    @NotEmpty(message = "密码不能为空")
    @JsonIgnore @NonNull
    private String password;

    @Column(columnDefinition = "VARCHAR(20)",nullable = false)
    @NotEmpty(message = "用户类型不能为空")
    @NonNull  private String userType;

    // 用户头像地址
    @Column(columnDefinition = "VARCHAR(200)")
    private String avatar;

    // 邮箱唯一
    @Column(columnDefinition = "VARCHAR(200)", unique=true, nullable = false)
    @NotEmpty(message = "邮箱地址不能为空")
    @NonNull  private String email;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    // 用户邮箱是否经过验证
    private boolean verified;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Temporal(TemporalType.TIMESTAMP) @CreatedDate
    @Column(nullable = false) 
    //注册日期
    private Date createdDate;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        // 这里是可以对用户进行多重身份授权的
        return AuthorityUtils.createAuthorityList(userType);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
