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

data class YouHaveBeenKickedToClientDTO(
    val playerId: String,
    val messageType: String = MsgType.YOU_HAVE_BEEN_KICKED_TO_CLIENT.value
): DTO()

data class ReadyUpToServer(
    var playerId: String,
    val messageType: String = MsgType.READY_UP_TO_SERVER.value
): DTO()

data class NotReadyToServer(
    var playerId: String,
    val messageType: String = MsgType.NOT_READY_TO_SERVER.value
): DTO()

enum class ServerInfoType(val value: String) {
    AVAILABLE_ROOMS("availableRooms")
}

data class RequestServerInfoToServer(
    var playerId: String,
    val messageType: String = MsgType.REQUEST_SERVER_INFO_TO_SERVER.value,
    val infoType: String
): DTO()

data class RoomsServerInfoToClientDTO(
    var playerId: String,
    val messageType: String = MsgType.ROOMS_SERVER_INFO_TO_CLIENT.value,
    val roomsData: List<RoomStateDTO>
): DTO()


