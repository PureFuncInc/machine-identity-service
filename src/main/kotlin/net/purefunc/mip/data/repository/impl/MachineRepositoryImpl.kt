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
    override suspend fun create(groups: String, label: String) =
        run {
            machineDao.findAvailable(groups).toList()[0].id!!
        }.apply {
            machineDao.setInUse(
                label = label,
                modifiedDate = Instant.now().toEpochMilli(),
                id = this,
            )
        }

    override suspend fun refresh(id: Long) =
        run {
            machineDao.findById(id)?.run {
                this.modifiedDate = Instant.now().toEpochMilli()
                machineDao.save(this)
            } ?: throw IllegalStateException("Machine $id not found.")
        }
}