package net.purefunc.mip.web.controller

import net.purefunc.mip.data.dto.req.MachineIdReq
import net.purefunc.mip.data.dto.req.MachineLabelReq
import net.purefunc.mip.data.repository.MachineRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1.0/machine")
class MachineController(
    private val machineRepository: MachineRepository,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    suspend fun postMachine(
        @RequestBody machineLabelReq: MachineLabelReq,
    ) = try {
        ResponseEntity.ok(machineLabelReq.create(machineRepository))
    } catch (ex: Exception) {
        handleException(ex)
    }

    @PatchMapping
    suspend fun patchMachine(
        @RequestBody machineIdReq: MachineIdReq,
    ) = try {
        ResponseEntity.ok(machineIdReq.refresh(machineRepository))
    } catch (ex: Exception) {
        handleException(ex)
    }

    private fun handleException(ex: Exception) =
        UUID.randomUUID().toString()
            .also {
                log.error("$this -> ${ex.message}", ex)
            }.run {
                ResponseEntity.internalServerError()
                    .header("error-log-uuid", this)
                    .body(mapOf("error-log-uuid" to this))
            }
}