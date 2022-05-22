package net.purefunc.mip.data.dto.req

import net.purefunc.mip.data.dao.MachineDao
import java.time.Instant

data class MachineIdReq(

    val id: Long,
) {

    suspend fun refresh(machineDao: MachineDao) =
        machineDao.findById(id)?.run {
            this.modifiedDate = Instant.now().toEpochMilli()
            machineDao.save(this)
        } ?: throw IllegalStateException("Machine $id not found.")
}
