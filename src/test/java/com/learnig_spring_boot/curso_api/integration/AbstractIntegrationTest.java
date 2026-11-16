package com.learnig_spring_boot.curso_api.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base class for integration tests using Testcontainers with PostgreSQL.
 *
 * Automatically manages a PostgreSQL container and provides Spring Boot
 * with connection details via @ServiceConnection.
 *
 * If Docker is not available in the current environment, tests inheriting
 * from this class will be skipped rather than failing the build.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    protected static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("curso_api_test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgres.start();
    }
}
