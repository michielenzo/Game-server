package network

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.javalin.websocket.WsSession
import main.kotlin.game.dto.SendGameStateToClientsDTO
import main.kotlin.game.dto.SendInputStateToServerDTO
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
        sessions.remove(session)
        NetworkNewsPaper.broadcast(buildDisconnectFromServerDTO(session))
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is SendGameStateToClientsDTO -> sendToAllSessions(convertDTOtoJSON(dto))
        }
    }

    override fun notifyLobbyNews(dto: DTO) {
        when(dto){
            is SendLobbyStateToClientsDTO -> sendToAllSessions(convertDTOtoJSON(dto))
        }
    }

    private fun convertDTOtoJSON(dto: DTO): String{
        return Gson().toJson(dto)
    }

    private fun buildConnectToServerDTO(session: WsSession): DTO {
        return ConnectionDTO(session.id, LocalDateTime.now())
    }

    private fun buildDisconnectFromServerDTO(session: WsSession): DTO {
        return DisconnectDTO(session.id, LocalDateTime.now())
    }

}

