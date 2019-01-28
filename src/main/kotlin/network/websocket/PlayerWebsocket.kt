package network

import io.javalin.websocket.WsSession
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DTO
import java.time.LocalDateTime


class PlayerWebsocket: Websocket(endPointPath = "/player", portNumber = 8080) {

    override fun onConnect(session: WsSession) {
        synchronized(PlayerWebsocket::class){
            session.send("Connected to the server")
            networkNewsPaper.broadcast(buildConnectToServerDTO(session))
        }
    }

    override fun onMessage(session: WsSession, message: String) {
        synchronized(PlayerWebsocket::class){
            session.send("message recieved")
        }
    }

    override fun onClose(session: WsSession, status: Int, message: String?) {
        synchronized(PlayerWebsocket::class){
            session.send("Disconnected from the server")
        }
    }

    private fun buildConnectToServerDTO(session: WsSession): DTO {
        return ConnectionDTO(session.id, LocalDateTime.now())
    }

}

