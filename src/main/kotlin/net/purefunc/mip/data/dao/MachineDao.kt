package net.purefunc.mip.data.dao

import kotlinx.coroutines.flow.Flow
import net.purefunc.mip.data.table.MachineDo
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MachineDao : CoroutineCrudRepository<MachineDo, Long> {

    @Query("SELECT m.* FROM machine m WHERE m.status = 'AVAILABLE' FOR UPDATE")
    fun findAvailable(): Flow<MachineDo>

    @Modifying
    @Query("UPDATE machine SET label = :label, status = 'IN_USE', modified_date = :modifiedDate WHERE id = :id")
    suspend fun setInUse(label: String, modifiedDate: Long, id: Long): Int

    @Modifying
    @Query("UPDATE machine SET label = '', status = 'AVAILABLE', modified_date = :modifiedDate WHERE status = 'IN_USE' AND modified_date < :invalidDate")
    suspend fun releaseInUse(modifiedDate: Long, invalidDate: Long): Int
}