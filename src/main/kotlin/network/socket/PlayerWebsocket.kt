package network

import io.javalin.websocket.WsSession

class PlayerWebsocket: Websocket(endPointPath = "/player", portNumber = 8080) {

    override fun onConnect(session: WsSession) {
        synchronized(PlayerWebsocket::class){
            session.send("Connected to the server")
            networkNewsPaper.broadcast("Client connected to the server")
        }
    }

    override fun onMessage(session: WsSession, message: String) {
        synchronized(PlayerWebsocket::class){
            session.send("message recieved")
            networkNewsPaper.broadcast(message)
        }
    }

    override fun onClose(session: WsSession, status: Int, message: String?) {
        synchronized(PlayerWebsocket::class){
            session.send("Disconnected from the server")
            networkNewsPaper.broadcast("Client disconnected from the server")
        }
    }

}

