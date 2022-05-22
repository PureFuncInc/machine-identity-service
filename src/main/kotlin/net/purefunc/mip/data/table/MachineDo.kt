package net.purefunc.mip.data.table

import net.purefunc.mip.data.dao.MachineDao
import net.purefunc.mip.data.enu.MachineStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("machine")
data class MachineDo(

    @Id
    val id: Long?,

    var label: String,

    var status: MachineStatus,

    var modifiedDate: Long,
) {

    companion object {
        suspend fun handle(machineDao: MachineDao) =
            machineDao.releaseInUse(
                modifiedDate = Instant.now().toEpochMilli(),
                invalidDate = Instant.now().toEpochMilli() - (60 * 1000),
            )
    }
}
