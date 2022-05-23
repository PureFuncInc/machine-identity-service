package net.purefunc.mip.integration

import kotlinx.coroutines.runBlocking
import net.purefunc.mip.data.dao.MachineDao
import net.purefunc.mip.data.dto.req.MachineIdReq
import net.purefunc.mip.data.dto.req.MachineLabelReq
import net.purefunc.mip.data.enu.MachineStatus
import net.purefunc.mip.data.table.MachineDo
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Testcontainers
class MachineWrongTest(
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

    @Autowired
    private lateinit var machineDao: MachineDao

    private val webTestClient =
        WebTestClient.bindToServer().baseUrl("http://localhost:$port/machine-identity-service").build()

    @Test
    @Order(1)
    internal fun init() =
        runBlocking {
            repeat(1024) {
                machineDao.save(MachineDo(null, "Group", "Service", MachineStatus.IN_USE, Instant.now().toEpochMilli()))
            }
        }

    @Test
    @Order(2)
    internal fun `test no available machine`() {
        webTestClient.post()
            .uri("/api/v1.0/machine")
            .bodyValue(MachineLabelReq("G1", "Test"))
            .exchange()
            .expectStatus().is5xxServerError
    }

    @Test
    @Order(3)
    internal fun `test wrong machine id`() {
        webTestClient.patch()
            .uri("/api/v1.0/machine")
            .bodyValue(MachineIdReq(1025))
            .exchange()
            .expectStatus().is5xxServerError
    }
}