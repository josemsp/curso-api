package com.learnig_spring_boot.curso_api.integration;

import com.learnig_spring_boot.curso_api.integration.AbstractIntegrationTest;
import com.learnig_spring_boot.curso_api.user.entity.User;
import com.learnig_spring_boot.curso_api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPersistAndFindUser(){
//        arrange: create a user
        User user = new User("John Doe", "john@example.com");

//        act: persist the user
        User savedUser = userRepository.saveAndFlush(user);

//        assert: verify that the user was persisted
        assertNotNull(savedUser.getId());

        User foundUser = userRepository
                .findByIdAndDeletedAtIsNull(savedUser.getId())
                .orElseThrow();

        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
        assertNotNull(foundUser.getCreatedAt());
    }

    @Test
    void shouldNotFindSoftDeletedUser(){
//        arrange
        User user = new User("Jane Doe", "jane@exmaple.com");
        User savedUser = userRepository.saveAndFlush(user);

//        act: soft delete the user
        savedUser.softDelete();
        userRepository.flush();

//        assert
        assertTrue(
                userRepository
                        .findByIdAndDeletedAtIsNull(savedUser.getId())
                        .isEmpty()
        );
    }
}

//package com.learnig_spring_boot.curso_api.integration;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.ApplicationContext;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.postgresql.PostgreSQLContainer;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
///**
// * Integration tests using a real PostgreSQL database.
// *
// * Testcontainers starts and manages the PostgreSQL container.
// * Spring Boot automatically obtains the database connection
// * through the ServiceConnection annotation.
// */
//@Testcontainers(disabledWithoutDocker = true)
//@SpringBootTest
//class UserIntegrationTest {
//    /**
//     * PostgreSQL container shared by all tests in this class.
//     *
//     * The container uses PostgreSQL 17, matching the major
//     * version used by our Supabase database.
//     */
//    @Container
//    @ServiceConnection
//    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine")
//            .withDatabaseName("curso_api_test")
//            .withUsername("test")
//            .withPassword("test");
//
//    /**
//     * Spring application context.
//     *
//     * If the context starts successfully, Spring Boot has
//     * connected to PostgreSQL and initialized its components.
//     */
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Test
//    void shouldLoadApplicationContext(){
//        assertNotNull(applicationContext);
//    }
//}
