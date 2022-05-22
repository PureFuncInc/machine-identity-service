package net.purefunc.mip.integration

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("generate-data")
@Testcontainers
class MachineBeanTest(
    @LocalServerPort port: Int,
) {

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

    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    internal fun `generate data test`() {
        log.info("test")
    }
}