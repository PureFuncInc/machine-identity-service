package net.purefunc.mip.data.repository.impl

import kotlinx.coroutines.flow.toList
import net.purefunc.mip.data.dao.MachineDao
import net.purefunc.mip.data.repository.MachineRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
class MachineRepositoryImpl(
    private val machineDao: MachineDao,
) : MachineRepository {

    @Transactional
    override suspend fun create(label: String) =
        run {
            machineDao.findAvailable().toList()[0].id!!
        }.apply {
            machineDao.setInUse(
                label = label,
                modifiedDate = Instant.now().toEpochMilli(),
                id = this
            )
        }
}