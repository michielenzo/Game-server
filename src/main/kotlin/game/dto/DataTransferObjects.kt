package main.kotlin.game.dto

import main.kotlin.newspaper.MessageType
import main.kotlin.utilities.DTO

data class SendGameStateDTO(val gameState: GameStateDTO,
                            val messageType: String = MessageType.SEND_GAME_STATE.value): DTO()

data class GameStateDTO(val players: MutableList<PlayerDTO> = mutableListOf())

data class PlayerDTO(val sessionId: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int): DTO()
