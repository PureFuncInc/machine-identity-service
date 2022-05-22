package net.purefunc.mip.data.dto.req

import net.purefunc.mip.data.repository.MachineRepository

data class MachineIdReq(

    val id: Long,
) {

    suspend fun refresh(
        machineRepository: MachineRepository,
    ) = machineRepository.refresh(id)
}
