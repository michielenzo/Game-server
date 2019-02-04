package main.kotlin.network.dto

import main.kotlin.utilities.DTO
import java.time.LocalDateTime

data class ConnectionDTO(val id: String, val timestamp: LocalDateTime): DTO()
