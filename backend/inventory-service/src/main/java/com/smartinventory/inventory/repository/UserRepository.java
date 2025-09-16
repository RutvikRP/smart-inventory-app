package com.smartinventory.inventory.repository;

import com.smartinventory.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
}
