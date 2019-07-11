package main.kotlin.game.spaceBalls.dto

import main.kotlin.game.spaceBalls.gameobjects.Player
import main.kotlin.newspaper.MessageType
import main.kotlin.utilities.DTO

data class SendGameStateToClientsDTO(val gameState: GameStateDTO,
                                     val messageType: String = MessageType.SEND_GAME_STATE_TO_ClIENTS.value): DTO()

data class GameStateDTO(val players: MutableList<PlayerDTO> = mutableListOf(),
                        val fireBalls: MutableList<FireBallDTO> = mutableListOf(),
                        val powerUps: MutableList<PowerUpDTO> = mutableListOf()): DTO()

data class PlayerDTO(val sessionId: String,
                     val name: String,
                     @Volatile var xPosition: Int,
                     @Volatile var yPosition: Int,
                     val health: Int,
                     val hasShield: Boolean,
                     val width: Int = Player.WIDTH,
                     val height: Int = Player.HEIGHT): DTO()

data class FireBallDTO(val xPosition: Int,
                       val yPosition: Int,
                       val diameter: Int)

data class PowerUpDTO(val type: String,
                      val xPosition: Int,
                      val yPosition: Int,
                      val width: Int,
                      val height: Int): DTO()

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