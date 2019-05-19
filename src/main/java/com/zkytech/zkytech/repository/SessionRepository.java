package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface SessionRepository extends JpaRepository<Session, Long> {
    int countSessionsByCreatedDateBetween(Date start,Date end);

}
