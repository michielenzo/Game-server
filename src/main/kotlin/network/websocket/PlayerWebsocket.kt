package network

import com.google.gson.Gson
import io.javalin.websocket.WsSession
import main.kotlin.game.dto.SendGameStateToClientsDTO
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.newspaper.gamestate.IGameStateNewsPaperSubscriber
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
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
        session.send("message recieved")
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

