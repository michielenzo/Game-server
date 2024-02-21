package network

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.javalin.websocket.WsContext
import main.kotlin.game.spaceBalls.dto.BackToLobbyToClientDTO
import main.kotlin.game.spaceBalls.dto.BackToLobbyToServerDTO
import main.kotlin.game.spaceBalls.dto.SendSpaceBallsGameStateToClientsDTO
import main.kotlin.game.spaceBalls.dto.SendInputStateToServerDTO
import main.kotlin.lobby.dto.ChooseGameModeToServerDTO
import main.kotlin.lobby.dto.ChooseNameToServerDTO
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.lobby.dto.StartGameToServerDTO
import main.kotlin.publisher.gamestate.IGameStateSubscriber
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.network.dto.HeartbeatAcknowledgeDTO
import main.kotlin.network.dto.HeartbeatCheckDTO
import main.kotlin.publisher.MessageType
import main.kotlin.publisher.gamestate.GameStatePublisher
import main.kotlin.publisher.lobby.ILobbySubscriber
import main.kotlin.publisher.lobby.LobbyPublisher
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.utilities.DTO
import java.time.LocalDateTime

class PlayerWebsocket: Websocket(), IGameStateSubscriber, ILobbySubscriber {

    init {
        GameStatePublisher.subscribe(this)
        LobbyPublisher.subscribe(this)
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

    private fun convertStringToDTObject(message: String, wsCtx: WsContext): DTO? {
        JsonParser()
                .parse(message)
                .asJsonObject
                .get(MessageType.MESSAGE_TYPE.value)
                .asString.also {messageType ->
            return when(messageType){
                MessageType.START_GAME_TO_SERVER.value -> Gson().fromJson(message, StartGameToServerDTO::class.java)
                MessageType.SEND_INPUT_STATE_TO_SERVER.value -> {
                    Gson().fromJson(message, SendInputStateToServerDTO::class.java).also {
                        it.sessionId = wsCtx.sessionId()
                    }
                }
                MessageType.CHOOSE_NAME_TO_SERVER.value -> {
                    Gson().fromJson(message, ChooseNameToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MessageType.BACK_TO_LOBBY_TO_SERVER.value -> {
                    Gson().fromJson(message, BackToLobbyToServerDTO::class.java).also {
                        it.playerId = wsCtx.sessionId()
                    }
                }
                MessageType.CHOOSE_GAMEMODE_TO_SERVER.value -> Gson().fromJson(message, ChooseGameModeToServerDTO::class.java)
                MessageType.HEARTBEAT_CHECK.value -> Gson().fromJson(message, HeartbeatCheckDTO::class.java)
                else -> {
                    throw Exception(String()
                            .plus("Invalid message received: ")
                            .plus(message)
                            .plus("\n"))
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

    override fun notifyLobbyNews(dto: DTO) {
        when(dto){
            is SendLobbyStateToClientsDTO -> sendToAllSessionsAndSetClientId(dto)
            is BackToLobbyToClientDTO -> sendToSessionById(dto.playerId, convertDTOtoJSON(dto))
        }
    }

    private fun buildConnectToServerDTO(session: WsContext): DTO {
        return ConnectionDTO(session.sessionId(), LocalDateTime.now())
    }

    private fun buildDisconnectFromServerDTO(session: WsContext): DTO {
        return DisconnectDTO(session.sessionId(), LocalDateTime.now())
    }
}

