package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * DataInitializer - Create default test users on application startup
 * Gracefully handles database unavailability
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepositoryJPA userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            initializeUsers();
        } catch (Exception e) {
            log.warn("⚠ Could not initialize test users (database may be unavailable): {}", e.getMessage());
            log.warn("⚠ Users will be created when the database becomes available via /api/auth/register");
        }
    }

    private void initializeUsers() {
        log.info("Initializing test users...");

        // Create test patient user if not exists
        if (!userRepository.existsByUsername("testuser")) {
            User testPatient = new User();
            testPatient.setId(UUID.randomUUID());
            testPatient.setUsername("testuser");
            testPatient.setEmail("test@example.com");
            testPatient.setPassword(passwordEncoder.encode("Test@123"));
            testPatient.setFirstName("Test");
            testPatient.setLastName("User");
            testPatient.setRole(UserRole.PATIENT);
            testPatient.setCreatedDate(System.currentTimeMillis());
            testPatient.setUpdatedDate(System.currentTimeMillis());
            
            userRepository.save(testPatient);
            log.info("✓ Test patient user created: testuser / Test@123");
        }

        // Create Chintu test user if not exists
        if (!userRepository.existsByUsername("Chintu_77")) {
            User chintuPatient = new User();
            chintuPatient.setId(UUID.randomUUID());
            chintuPatient.setUsername("Chintu_77");
            chintuPatient.setEmail("chintu@example.com");
            chintuPatient.setPassword(passwordEncoder.encode("Chintu@123"));
            chintuPatient.setFirstName("Chintu");
            chintuPatient.setLastName("Kumar");
            chintuPatient.setRole(UserRole.PATIENT);
            chintuPatient.setCreatedDate(System.currentTimeMillis());
            chintuPatient.setUpdatedDate(System.currentTimeMillis());
            
            userRepository.save(chintuPatient);
            log.info("✓ Test patient user created: Chintu_77 / Chintu@123");
        }

        // Create test doctor user if not exists
        if (!userRepository.existsByUsername("doctor1")) {
            User testDoctor = new User();
            testDoctor.setId(UUID.randomUUID());
            testDoctor.setUsername("doctor1");
            testDoctor.setEmail("doctor@hospital.com");
            testDoctor.setPassword(passwordEncoder.encode("Doctor@123"));
            testDoctor.setFirstName("Dr.");
            testDoctor.setLastName("Smith");
            testDoctor.setRole(UserRole.DOCTOR);
            testDoctor.setCreatedDate(System.currentTimeMillis());
            testDoctor.setUpdatedDate(System.currentTimeMillis());
            
            userRepository.save(testDoctor);
            log.info("✓ Test doctor user created: doctor1 / Doctor@123");
        }

        log.info("Test data initialization completed!");
    }
}
