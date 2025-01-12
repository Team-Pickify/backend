package com.pickyfy.pickyfy.repository;

import com.pickyfy.pickyfy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String username);

    Optional<User> findByEmail(String email);
}