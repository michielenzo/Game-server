package main.kotlin.network.dto

import main.kotlin.publisher.MessageType
import main.kotlin.utilities.DTO
import java.time.LocalDateTime

data class ConnectionDTO(val id: String, val timestamp: LocalDateTime): DTO()

data class DisconnectDTO(val id: String, val timestamp: LocalDateTime): DTO()

data class HeartbeatCheckDTO(val messageType: String = MessageType.HEARTBEAT_CHECK.value): DTO()
data class HeartbeatAcknowledgeDTO(val messageType: String = MessageType.HEARTBEAT_ACKNOWLEDGE.value): DTO()
