package network

import com.google.gson.Gson
import io.javalin.websocket.WsSession
import main.kotlin.game.dto.SendGameStateDTO
import main.kotlin.newspaper.gamestate.IGameStateNewsPaperSubscriber
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO
import java.time.LocalDateTime


class PlayerWebsocket: Websocket(endPointPath = "/player", portNumber = 8080), IGameStateNewsPaperSubscriber {

    init {
        GameStateNewsPaper.subscribe(this)
    }

    override fun onConnect(session: WsSession) {
        sessions.add(session)
        NetworkNewsPaper.broadcast(buildConnectToServerDTO(session))
    }

    override fun onMessage(session: WsSession, message: String) {
        session.send("message recieved")
    }

    override fun onClose(session: WsSession, status: Int, message: String?) {
        session.send("Disconnected from the server")
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is SendGameStateDTO -> sendGameState(dto)
        }
    }

    private fun sendGameState(sendGameStateDTO: SendGameStateDTO) {
        Gson().toJson(sendGameStateDTO).also {
            sendToAllSessions(it)
        }
    }

    private fun buildConnectToServerDTO(session: WsSession): DTO {
        return ConnectionDTO(session.id, LocalDateTime.now())
    }

}

