package net.purefunc.mip

import kotlinx.coroutines.runBlocking
import net.purefunc.mip.data.dao.MachineDao
import net.purefunc.mip.data.enu.MachineStatus
import net.purefunc.mip.data.table.MachineDo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@SpringBootTest
@ActiveProfiles("generate-test")
class GenerateData {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var machineDao: MachineDao

    //    @Test
    fun process() =
        runBlocking {
            repeat(1024) {
                machineDao.save(MachineDo(null, "", "", MachineStatus.AVAILABLE, Instant.now().toEpochMilli()))
            }

            log.info("Add Test Data Finished.")
        }
}