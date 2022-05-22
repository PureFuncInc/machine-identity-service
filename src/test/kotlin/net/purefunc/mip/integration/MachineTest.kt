package net.purefunc.mip.integration

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.purefunc.mip.data.dao.MachineDao
import net.purefunc.mip.data.dto.req.MachineIdReq
import net.purefunc.mip.data.dto.req.MachineLabelReq
import net.purefunc.mip.data.enu.MachineStatus
import net.purefunc.mip.data.table.MachineDo
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.LoggerFactory
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
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Testcontainers
class MachineTest(
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

    @Autowired
    private lateinit var machineDao: MachineDao

    private val webTestClient =
        WebTestClient.bindToServer().baseUrl("http://localhost:$port/machine-identity-service").build()

    @Test
    @Order(1)
    internal fun init() =
        runBlocking {
            repeat(1024) {
                machineDao.save(MachineDo(null, "", MachineStatus.AVAILABLE, Instant.now().toEpochMilli()))
            }
        }

    @Test
    @Order(2)
    internal fun testMachine() =
        runBlocking {
            val idDateMap = (1..10).map {
                webTestClient.post()
                    .uri("/api/v1.0/machine")
                    .bodyValue(MachineLabelReq("Test"))
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(Long::class.java).returnResult().responseBody!!
            }.map { id ->
                val start = Instant.now().toEpochMilli()
                val machine = machineDao.findById(id) ?: throw IllegalStateException()
                Assertions.assertThat(machine.label).isEqualTo("Test")
                Assertions.assertThat(machine.status).isEqualTo(MachineStatus.IN_USE)
                Assertions.assertThat(machine.modifiedDate < start).isTrue
                Pair(id, start)
            }

            log.info("Sleep 30s then refresh id 1 ~ 5.")
            TimeUnit.SECONDS.sleep(30)

            idDateMap.subList(0, 5).map { idDate ->
                webTestClient.patch()
                    .uri("/api/v1.0/machine")
                    .bodyValue(MachineIdReq(idDate.first))
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(MachineDo::class.java).returnResult().responseBody!!
            }.map { machine ->
                Assertions.assertThat(machine.modifiedDate > idDateMap[0].second).isTrue
            }

            log.info("Sleep 45s for waiting id 5 ~ 10 timeout invalid.")
            TimeUnit.SECONDS.sleep(45)

            MachineDo.handle(machineDao)

            idDateMap.subList(5, 10).map { idDate ->
                val start = Instant.now().toEpochMilli()
                val machine = machineDao.findById(idDate.first) ?: throw IllegalStateException()
                Assertions.assertThat(machine.label).isEqualTo("")
                Assertions.assertThat(machine.status).isEqualTo(MachineStatus.AVAILABLE)
                Assertions.assertThat(machine.modifiedDate < start).isTrue
            }

            Assertions.assertThat(machineDao.findAllByStatus(MachineStatus.IN_USE).toList().size).isEqualTo(5)

            log.info("Finished.")
        }
}