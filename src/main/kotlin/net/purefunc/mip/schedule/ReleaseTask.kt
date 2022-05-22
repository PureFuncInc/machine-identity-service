package net.purefunc.mip.schedule

import kotlinx.coroutines.runBlocking
import net.purefunc.mip.data.dao.MachineDao
import net.purefunc.mip.data.table.MachineDo
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@ConditionalOnProperty(name = ["schedule.enabled"], havingValue = "true")
@Service
class ReleaseTask(
    private val machineDao: MachineDao,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 */1 * * * *")
    fun check() =
        runBlocking {
            log.debug("Release IN_USE")

            MachineDo.handle(machineDao)
        }
}