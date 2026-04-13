package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * User Repository - JPA interface for User entity (PostgreSQL)
 */
@Repository
public interface UserRepositoryJPA extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
}
