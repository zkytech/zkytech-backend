package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserById(Long id);

    @Query(value="select avatar from User u where u.id = ?1")
    String findAvatarById(Long id);

    @Query(value = "select username from User u where u.id = ?1")
    String findUsernameById(Long id);

    @Query(value = "select u.id from User u where u.userType = 'ADMIN'")
    Long findAdminUserId();
}
