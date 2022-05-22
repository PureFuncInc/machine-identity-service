package net.purefunc.mip.data.table

import net.purefunc.mip.data.enu.MachineStatus
import org.springframework.data.relational.core.mapping.Table

@Table("machine")
data class MachineDo(

    val id: Long?,

    var label: String,

    var status: MachineStatus,

    var modifiedDate: Long,
)
