package main.kotlin.network.dto

import java.time.LocalDateTime

abstract class DTO

data class ConnectionDTO(val id: String, val timestamp: LocalDateTime): DTO()
