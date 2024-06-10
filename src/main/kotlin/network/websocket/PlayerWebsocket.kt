package network

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.javalin.websocket.WsContext
import main.kotlin.game.spaceBalls.dto.BackToRoomToClientDTO
import main.kotlin.game.spaceBalls.dto.BackToRoomToServerDTO
import main.kotlin.game.spaceBalls.dto.SendSpaceBallsGameStateToClientsDTO
import main.kotlin.game.spaceBalls.dto.SendInputStateToServerDTO
import main.kotlin.publisher.gamestate.IGameStateSubscriber
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.network.dto.HeartbeatAcknowledgeDTO
import main.kotlin.network.dto.HeartbeatCheckDTO
import main.kotlin.publisher.MsgType
import main.kotlin.publisher.gamestate.GameStatePublisher
import main.kotlin.publisher.room.IRoomSubscriber
import main.kotlin.publisher.room.RoomPublisher
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.room.dto.*
import main.kotlin.utilities.DTO
import java.time.LocalDateTime

class PlayerWebsocket: Websocket(), IGameStateSubscriber, IRoomSubscriber {

    init {
        GameStatePublisher.subscribe(this)
        RoomPublisher.subscribe(this)
    }

    override fun onConnect(wsCtx: WsContext) {
        sessions.add(wsCtx)
        NetworkPublisher.broadcast(buildConnectToServerDTO(wsCtx))
    }

    override fun onMessage(wsCtx: WsContext, message: String) {
        convertStringToDTObject(message, wsCtx).also {
            it?: return

            if(it is HeartbeatCheckDTO) {
                HeartbeatAcknowledgeDTO().also { dto ->
                    sendToSessionById(wsCtx.sessionId(), convertDTOtoJSON(dto))
                }
                return
            }

            NetworkPublisher.broadcast(it)
        }
    }

    private fun convertStringToDTObject(msg: String, wsCtx: WsContext): DTO? {
        JsonParser()
                .parse(msg)
                .asJsonObject
                .get(MsgType.MESSAGE_TYPE.value)
                .asString.also {messageType ->
            return when(messageType){
                MsgType.START_GAME_TO_SERVER.value -> Gson().fromJson(msg, StartGameToServerDTO::class.java)
                MsgType.SEND_INPUT_STATE_TO_SERVER.value -> {
                    Gson().fromJson(msg, SendInputStateToServerDTO::class.java).also {
                        it.sessionId = wsCtx.sessionId()
                    }
                }
                MsgType.CHOOSE_NAME_TO_SERVER.value -> {
                    Gson().fromJson(msg, ChooseNameToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.BACK_TO_ROOM_TO_SERVER.value -> {
                    Gson().fromJson(msg, BackToRoomToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.JOIN_ROOM_TO_SERVER.value -> {
                    Gson().fromJson(msg, JoinRoomToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.KICK_PLAYER_TO_SERVER.value -> {
                    Gson().fromJson(msg, KickPlayerToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.PROMOTE_PLAYER_TO_SERVER.value -> {
                    Gson().fromJson(msg, PromotePlayerToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.READY_UP_TO_SERVER.value -> {
                    Gson().fromJson(msg, ReadyUpToServer::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.NOT_READY_TO_SERVER.value -> {
                    Gson().fromJson(msg, NotReadyToServer::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MsgType.CHOOSE_GAMEMODE_TO_SERVER.value ->
                    Gson().fromJson(msg, ChooseGameModeToServerDTO::class.java)
                MsgType.HEARTBEAT_CHECK.value -> Gson().fromJson(msg, HeartbeatCheckDTO::class.java)
                else -> {
                    println(String()
                        .plus("Invalid message received: ")
                        .plus(msg)
                        .plus("\n"))
                    return null
                }
            }
        }
    }

    override fun onClose(wsCtx: WsContext, status: Int, message: String?) {
        NetworkPublisher.broadcast(buildDisconnectFromServerDTO(wsCtx))
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is SendSpaceBallsGameStateToClientsDTO -> {
                mutableSetOf<WsContext>().also { set ->
                    dto.gameState.players.forEach { player ->
                        sessions.find { sesh -> sesh.sessionId() == player.sessionId }.also {
                            if (it != null) {
                                set.add(it)
                            }
                        }
                    }
                    sendToSessionSet(set, convertDTOtoJSON(dto))
                }
            }
        }
    }

    override fun notifyRoomNews(dto: DTO) {
        when(dto){
            is SendRoomStateToClientsDTO -> sendToAllRoomSessionsAndSetClientId(dto)
            is BackToRoomToClientDTO -> sendToSessionById(dto.playerId, convertDTOtoJSON(dto))
            is RoomNotFoundToClientDTO -> sendToSessionById(dto.playerId, convertDTOtoJSON(dto))
            is YouHaveBeenKickedToClientDTO -> sendToSessionById(dto.playerId, convertDTOtoJSON(dto))
        }
    }

    private fun buildConnectToServerDTO(session: WsContext): DTO {
        val roomCode = session.queryParam("roomCode")
        return ConnectionDTO(session.sessionId(), roomCode, LocalDateTime.now())
    }

    private fun buildDisconnectFromServerDTO(session: WsContext): DTO {
        return DisconnectDTO(session.sessionId(), LocalDateTime.now())
    }
}

