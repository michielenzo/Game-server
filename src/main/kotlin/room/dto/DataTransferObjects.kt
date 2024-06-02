package main.kotlin.room.dto

import main.kotlin.publisher.MsgType
import main.kotlin.utilities.DTO

data class SendRoomStateToClientsDTO(
    val roomState: RoomStateDTO,
    var yourId: String,
    val messageType: String = MsgType.SEND_ROOM_STATE_TO_CLIENTS.value
): DTO()

data class RoomStateDTO(
    val gameMode: String,
    val roomCode: String,
    val leaderId: String,
    val players: MutableList<PlayerDTO> = mutableListOf()
): DTO()

data class PlayerDTO(
    val id: String,
    val status: String,
    val name: String = id
): DTO()

data class KickPlayerToServerDTO(
    var playerId: String,
    val playerToKickId: String,
    val messageType: String = MsgType.KICK_PLAYER_TO_SERVER.value
): DTO()

data class PromotePlayerToServerDTO(
    var playerId: String,
    val playerToPromoteId: String,
    val messageType: String = MsgType.PROMOTE_PLAYER_TO_SERVER.value
): DTO()

data class StartGameToServerDTO(
    val playerId: String,
    val messageType: String = MsgType.START_GAME_TO_SERVER.value
): DTO()

data class ChooseNameToServerDTO(
    var playerId: String,
    val chosenName: String,
    val messageType: String = MsgType.CHOOSE_NAME_TO_SERVER.value
): DTO()

data class ChooseGameModeToServerDTO(
    val game: String,
    val playerId: String,
    val messageType: String = MsgType.CHOOSE_GAMEMODE_TO_SERVER.value
): DTO()

data class JoinRoomToServerDTO(
    var playerId: String,
    val playerName: String,
    val roomCode: String,
    val messageType: String = MsgType.JOIN_ROOM_TO_SERVER.value
): DTO()

data class RoomNotFoundToClientDTO(
    val roomCode: String,
    val playerId: String,
    val messageType: String = MsgType.ROOM_NOT_FOUND_TO_CLIENT.value
): DTO()

