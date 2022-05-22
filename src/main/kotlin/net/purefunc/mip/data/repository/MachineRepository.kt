package net.purefunc.mip.data.repository

interface MachineRepository {

    suspend fun create(label: String): Long
}