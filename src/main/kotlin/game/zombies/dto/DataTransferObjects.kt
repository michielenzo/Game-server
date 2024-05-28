package main.kotlin.game.zombies.dto

import main.kotlin.publisher.MsgType
import main.kotlin.utilities.DTO

data class SendZombiesGameStateToClientsDTO(val gameState: GameStateDTO,
                                            val messageType: String = MsgType.SEND_ZOMBIES_GAME_STATE_TO_CLIENTS.value): DTO()

data class GameStateDTO(val players: List<PlayerDTO> = mutableListOf()): DTO()

data class PlayerDTO(val sessionId: String,
                     val name: String,
                     val xPos: Int,
                     val yPos: Int): DTO()