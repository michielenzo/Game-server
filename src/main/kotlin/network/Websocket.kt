package network

import io.javalin.Javalin
import io.javalin.websocket.WsSession

abstract class Websocket(var endPointPath: String, var portNumber: Int) {

    fun initialize(){
        Javalin.create().apply {
            ws(endPointPath){ ws ->
                ws.onConnect { session ->
                    onConnect(session)
                }
                ws.onMessage { session, message ->
                    onMessage(session, message)
                }
                ws.onClose { session, status, message ->
                    onClose(session, status, message)
                }
            }
            start(portNumber)
        }
    }

    abstract fun onClose(session: WsSession, status: Int, message: String?)
    abstract fun onMessage(session: WsSession, message: String)
    abstract fun onConnect(session: WsSession)

}