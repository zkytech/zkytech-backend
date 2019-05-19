package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.MailVerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailVerifyCodeRepository extends JpaRepository<MailVerifyCode,Long> {
    MailVerifyCode findMailVerifyCodeByCode(String code);
}
