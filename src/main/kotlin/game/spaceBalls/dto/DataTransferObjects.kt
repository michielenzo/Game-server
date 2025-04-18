package main.kotlin.game.spaceBalls.dto

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.spaceBalls.gameobjects.Meteorite
import main.kotlin.publisher.MsgType
import main.kotlin.utilities.DTO

data class SendSpaceBallsGameStateToClientsDTO(
    val gameState: GameStateDTO,
    val messageType: String = MsgType.SEND_SPACE_BALLS_GAME_STATE_TO_CLIENTS.value
): DTO()

data class GameStateDTO(
    val players: MutableList<PlayerDTO> = mutableListOf(),
    val meteorites: MutableList<MeteoriteDTO> = mutableListOf(),
    val powerUps: MutableList<PowerUpDTO> = mutableListOf(),
    val homingBalls: MutableList<HomingBallDTO> = mutableListOf(),
    val events: MutableList<GameEventDTO> = mutableListOf()
): DTO()

data class GameEventDTO (
    val type: GameEventType,
    val data: HashMap<String, String> = HashMap()
): DTO()

data class PlayerDTO(
    val id: Int,
    val sessionId: String,
    val name: String,
    @Volatile var x: Double,
    @Volatile var y: Double,
    val health: Int,
    val shield: Boolean,
    val inverted: Boolean
): DTO()

data class MeteoriteDTO(
    val id: Int,
    val x: Double,
    val y: Double,
    val state: Meteorite.State
)

data class PowerUpDTO(
    val type: String,
    val x: Double,
    val y: Double
): DTO()

data class HomingBallDTO(
    val id: Int,
    val x: Double,
    val y: Double
)

data class SendInputStateToServerDTO(
    var sessionId: String,
    val wKey: Boolean,
    val aKey: Boolean,
    val sKey: Boolean,
    val dKey: Boolean,
    val messageType: String = MsgType.SEND_INPUT_STATE_TO_SERVER.value
): DTO()

data class BackToRoomToServerDTO(
    val messageType: String = MsgType.BACK_TO_ROOM_TO_SERVER.value,
    var playerId: String
): DTO()

data class BackToRoomToClientDTO(
    val messageType: String = MsgType.BACK_TO_ROOM_TO_CLIENT.value,
    var playerId: String? = null
): DTO()

data class GameConfigToClientsDTO(
    val messageType: String = MsgType.GAME_CONFIG_TO_CLIENTS.value,
    val powerUpWidth: Double,
    val powerUpHeight: Double,
    val playerWidth: Int,
    val playerHeight: Int,
    val homingBallRadius: Int,
    val meteoriteDiameter: Double,
    val playerSpeed: Int,
    val countdownMillis: Long,
    val meteoritesDirectionInit: List<MeteoriteDirectionDTO>
): DTO()

data class MeteoriteDirectionDTO(
    val id: Int,
    val direction: String
): DTO()

data class SetServerTickRateToServer(
    val tickRate: Double,
    var playerId: String,
): DTO()

data class ServerTickRateChangedToClientDTO(
    val tickRate: Double,
    val messageType: String = MsgType.SERVER_TICK_RATE_CHANGED_TO_CLIENT.value
) : DTO ()
