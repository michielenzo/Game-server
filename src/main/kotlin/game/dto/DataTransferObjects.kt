package main.kotlin.game.dto

import main.kotlin.newspaper.MessageType
import main.kotlin.utilities.DTO

data class NewPlayerDTO(val sessionId: String,
                        val xPosition: Int,
                        val yPosition: Int,
                        val messageType: String = MessageType.NEW_PLAYER.value): DTO()

