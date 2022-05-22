package net.purefunc.mip.data.dto.req

import net.purefunc.mip.data.repository.MachineRepository

data class MachineLabelReq(

    val label: String,
) {

    suspend fun create(machineRepository: MachineRepository) = machineRepository.create(label)
}