package net.purefunc.mip.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class MachineTest {

    companion object {
        @Container
        private val postgreSQLContainer = PostgreSQLContainer("postgres:13")
            .withInitScript("schema.sql")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            dynamicPropertyRegistry.add("spring.r2dbc.url") { "r2dbc:postgresql://${postgreSQLContainer.host}:${postgreSQLContainer.firstMappedPort}/${postgreSQLContainer.databaseName}" }
            dynamicPropertyRegistry.add("spring.r2dbc.username", postgreSQLContainer::getUsername)
            dynamicPropertyRegistry.add("spring.r2dbc.password", postgreSQLContainer::getPassword)
        }
    }
}