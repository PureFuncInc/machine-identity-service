package net.purefunc.mip.data.dto.req

import net.purefunc.mip.data.repository.MachineRepository

data class MachineLabelReq(

    val groups: String,

    val label: String,
) {

    suspend fun create(machineRepository: MachineRepository) = machineRepository.create(groups, label)
}