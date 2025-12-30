package com.dwincomputer.kasir.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dwincomputer.kasir.auth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.customPermissions WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}