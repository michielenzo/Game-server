package main.kotlin.game.spaceBalls.dto

import main.kotlin.publisher.MessageType
import main.kotlin.utilities.DTO

data class SendSpaceBallsGameStateToClientsDTO(
    val gameState: GameStateDTO,
    val messageType: String = MessageType.SEND_SPACE_BALLS_GAME_STATE_TO_CLIENTS.value): DTO()

data class GameStateDTO(val players: MutableList<PlayerDTO> = mutableListOf(),
                        val fireBalls: MutableList<FireBallDTO> = mutableListOf(),
                        val powerUps: MutableList<PowerUpDTO> = mutableListOf(),
                        val homingBalls: MutableList<HomingBallDTO> = mutableListOf()): DTO()

data class PlayerDTO(val id: Int,
                     val sessionId: String,
                     val name: String,
                     @Volatile var x: Double,
                     @Volatile var y: Double,
                     val health: Int,
                     val shield: Boolean,
                     val inverted: Boolean): DTO()

data class FireBallDTO(val id: Int,
                       val x: Double,
                       val y: Double)

data class PowerUpDTO(val type: String,
                      val x: Double,
                      val y: Double): DTO()

data class HomingBallDTO(val id: Int,
                         val x: Double,
                         val y: Double)

data class SendInputStateToServerDTO(var sessionId: String,
                                     val wKey: Boolean,
                                     val aKey: Boolean,
                                     val sKey: Boolean,
                                     val dKey: Boolean,
                                     val messageType: String = MessageType.SEND_INPUT_STATE_TO_SERVER.value): DTO()

data class BackToLobbyToServerDTO(val messageType: String = MessageType.BACK_TO_LOBBY_TO_SERVER.value,
                                  var playerId: String? = null): DTO()

data class BackToLobbyToClientDTO(val messageType: String = MessageType.BACK_TO_LOBBY_TO_CLIENT.value,
                                  var playerId: String? = null): DTO()