package main.kotlin.game.dto

import main.kotlin.game.Player
import main.kotlin.newspaper.MessageType
import main.kotlin.utilities.DTO

data class SendGameStateToClientsDTO(val gameState: GameStateDTO,
                                     val messageType: String = MessageType.SEND_GAME_STATE_TO_ClIENTS.value): DTO()

data class GameStateDTO(val players: MutableList<PlayerDTO> = mutableListOf()): DTO()

data class PlayerDTO(val sessionId: String,
                     @Volatile var xPosition: Int,
                     @Volatile var yPosition: Int,
                     val width: Int = Player.WIDTH,
                     val height: Int = Player.HEIGHT): DTO()
