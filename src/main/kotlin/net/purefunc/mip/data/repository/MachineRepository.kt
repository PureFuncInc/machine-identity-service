package net.purefunc.mip.data.repository

import net.purefunc.mip.data.table.MachineDo

interface MachineRepository {

    suspend fun create(label: String): Long

    suspend fun refresh(id: Long): MachineDo
}