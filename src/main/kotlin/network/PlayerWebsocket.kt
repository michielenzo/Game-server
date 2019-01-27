package network

import io.javalin.websocket.WsSession

class PlayerWebsocket: Websocket(endPointPath = "/player", portNumber = 8080) {

    override fun onConnect(session: WsSession) {
        synchronized(PlayerWebsocket::class){
            session.send("Connected to the server")
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

}

