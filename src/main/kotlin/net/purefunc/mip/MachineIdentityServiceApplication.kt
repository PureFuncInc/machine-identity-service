package net.purefunc.mip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MachineIdentityServiceApplication

fun main(args: Array<String>) {
    runApplication<MachineIdentityServiceApplication>(*args)
}
