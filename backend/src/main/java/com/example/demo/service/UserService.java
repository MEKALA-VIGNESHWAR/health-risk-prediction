package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserService - Business logic for User operations (Supabase PostgreSQL)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepositoryJPA userRepository;

    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating user with username: {}", userDTO.getUsername());
        
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setCreatedDate(System.currentTimeMillis());
        user.setUpdatedDate(System.currentTimeMillis());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(UUID id) {
        log.info("Fetching user with id: {}", id);
        if (id == null) {
            log.warn("User ID is null");
            return null;
        }
        Optional<User> user = userRepository.findById(id);
        return user.map(this::convertToDTO).orElse(null);
    }

    public UserDTO getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(this::convertToDTO).orElse(null);
    }

    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        log.info("Updating user with id: {}", id);
        if (id == null) {
            log.warn("User ID is null");
            return null;
        }
        Optional<User> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(userDTO.getEmail());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setUpdatedDate(System.currentTimeMillis());
            
            User updatedUser = userRepository.save(user);
            log.info("User updated successfully");
            return convertToDTO(updatedUser);
        }
        
        log.warn("User not found with id: {}", id);
        return null;
    }

    public boolean deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        if (id == null) {
            log.warn("User ID is null");
            return false;
        }
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User deleted successfully");
            return true;
        }
        log.warn("User not found with id: {}", id);
        return false;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                null, // Don't expose password in DTO
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }

}

