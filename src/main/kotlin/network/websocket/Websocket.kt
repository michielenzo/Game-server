package network

import io.javalin.Javalin
import io.javalin.websocket.WsSession

abstract class Websocket(var endPointPath: String, var portNumber: Int) {

    protected val sessions = mutableListOf<WsSession>()

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

    @Synchronized fun sendToAllSessions(message: String){
        sessions.forEach { sesh ->
            if(sesh.isOpen) sesh.send(message)
        }
    }

    @Synchronized fun sendToSessionById(sessionId: String?, message: String){
        sessions.find { sesh -> sesh.id == sessionId }.also { sesh ->
            sesh?: return
            if(sesh.isOpen) sesh.send(message)
        }
    }

    @Synchronized fun sendToSessionSet(sessionSet: Set<WsSession>, message: String){
        sessionSet.forEach { sesh ->
            if(sesh.isOpen) sesh.send(message)
        }
    }

    abstract fun onClose(session: WsSession, status: Int, message: String?)
    abstract fun onMessage(session: WsSession, message: String)
    abstract fun onConnect(session: WsSession)

}