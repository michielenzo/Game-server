package network

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.javalin.websocket.WsSession
import main.kotlin.game.spaceBalls.dto.BackToLobbyToClientDTO
import main.kotlin.game.spaceBalls.dto.BackToLobbyToServerDTO
import main.kotlin.game.spaceBalls.dto.SendSpaceBallsGameStateToClientsDTO
import main.kotlin.game.spaceBalls.dto.SendInputStateToServerDTO
import main.kotlin.game.zombies.dto.SendZombiesGameStateToClientsDTO
import main.kotlin.lobby.dto.ChooseGameModeToServerDTO
import main.kotlin.lobby.dto.ChooseNameToServerDTO
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.lobby.dto.StartGameToServerDTO
import main.kotlin.newspaper.gamestate.IGameStateNewsPaperSubscriber
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.MessageType
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.lobby.ILobbyNewsPaperSubscriber
import main.kotlin.newspaper.lobby.LobbyNewsPaper
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO
import java.time.LocalDateTime


class PlayerWebsocket: Websocket(endPointPath = "/player", portNumber = 8080), IGameStateNewsPaperSubscriber, ILobbyNewsPaperSubscriber {

    init {
        GameStateNewsPaper.subscribe(this)
        LobbyNewsPaper.subscribe(this)
    }

    override fun onConnect(session: WsSession) {
        sessions.add(session)
        NetworkNewsPaper.broadcast(buildConnectToServerDTO(session))
    }

    override fun onMessage(session: WsSession, message: String) {
        convertStringToDTObject(message, session).also {
            it?: return
            NetworkNewsPaper.broadcast(it)
        }
    }

    private fun convertStringToDTObject(message: String, session: WsSession): DTO? {
        JsonParser()
                .parse(message)
                .asJsonObject
                .get(MessageType.MESSAGE_TYPE.value)
                .asString.also {messageType ->
            return when(messageType){
                MessageType.START_GAME_TO_SERVER.value -> Gson().fromJson(message, StartGameToServerDTO::class.java)
                MessageType.SEND_INPUT_STATE_TO_SERVER.value -> {
                    Gson().fromJson(message, SendInputStateToServerDTO::class.java).also {
                        it.sessionId = session.id
                    }
                }
                MessageType.CHOOSE_NAME_TO_SERVER.value -> {
                    Gson().fromJson(message, ChooseNameToServerDTO::class.java).also {
                        it.playerId = session.id
                    }
                }
                MessageType.BACK_TO_LOBBY_TO_SERVER.value -> {
                    Gson().fromJson(message, BackToLobbyToServerDTO::class.java).also {
                        it.playerId = session.id
                    }
                }
                MessageType.CHOOSE_GAMEMODE_TO_SERVER.value -> Gson().fromJson(message, ChooseGameModeToServerDTO::class.java)
                else -> {
                    throw Exception(String()
                            .plus("Invalid message received: ")
                            .plus(message)
                            .plus("\n"))
                }
            }
        }
       return null
    }

    override fun onClose(session: WsSession, status: Int, message: String?) {
        NetworkNewsPaper.broadcast(buildDisconnectFromServerDTO(session))
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is SendSpaceBallsGameStateToClientsDTO -> {
                mutableSetOf<WsSession>().also { set ->
                    dto.gameState.players.forEach {player ->
                        sessions.find { sesh -> sesh.id == player.sessionId }.also {
                            if (it != null) {
                                set.add(it)
                            }
                        }
                    }
                    sendToSessionSet(set, convertDTOtoJSON(dto))
                }
            }
            is SendZombiesGameStateToClientsDTO -> {
                mutableSetOf<WsSession>().also { set ->
                    dto.gameState.players.forEach {player ->
                        sessions.find { sesh -> sesh.id == player.sessionId }.also {
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

    private fun buildConnectToServerDTO(session: WsSession): DTO {
        return ConnectionDTO(session.id, LocalDateTime.now())
    }

    private fun buildDisconnectFromServerDTO(session: WsSession): DTO {
        return DisconnectDTO(session.id, LocalDateTime.now())
    }
}

