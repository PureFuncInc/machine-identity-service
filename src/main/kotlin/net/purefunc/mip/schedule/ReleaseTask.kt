package net.purefunc.mip.schedule

import kotlinx.coroutines.runBlocking
import net.purefunc.mip.data.dao.MachineDao
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ReleaseTask(
    private val machineDao: MachineDao,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 */1 * * * *")
    fun check() =
        runBlocking {
            log.debug("Release IN_USE")

            machineDao.releaseInUse(
                modifiedDate = Instant.now().toEpochMilli(),
                invalidDate = Instant.now().toEpochMilli() - (60 * 1000),
            )
        }
}