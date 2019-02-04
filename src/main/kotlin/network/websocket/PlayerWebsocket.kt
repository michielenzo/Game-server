package network

import com.google.gson.Gson
import io.javalin.websocket.WsSession
import main.kotlin.game.dto.NewPlayerDTO
import main.kotlin.newspaper.gamestate.IGameStateNewsPaperSubscriber
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.utilities.DTO
import java.time.LocalDateTime


class PlayerWebsocket: Websocket(endPointPath = "/player", portNumber = 8080), IGameStateNewsPaperSubscriber {

    init {
        GameStateNewsPaper.getInstance().subscribe(this)
    }

    override fun onConnect(session: WsSession) {
        sessions.add(session)
        session.send("Connected to the server")
        networkNewsPaper.broadcast(buildConnectToServerDTO(session))
    }

    override fun onMessage(session: WsSession, message: String) {
        session.send("message recieved")
    }

    override fun onClose(session: WsSession, status: Int, message: String?) {
        session.send("Disconnected from the server")
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is NewPlayerDTO -> handleNewPlayerMessage(dto)
        }
    }

    private fun handleNewPlayerMessage(newPlayerDTO: NewPlayerDTO) {
        Gson().toJson(newPlayerDTO).also {
            sendToAllSessions(it)
        }
    }

    private fun buildConnectToServerDTO(session: WsSession): DTO {
        return ConnectionDTO(session.id, LocalDateTime.now())
    }

}

