package network

import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.websocket.WsSession
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.utilities.DTO

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

    @Synchronized fun sendToAllSessionsAndSetClientId(dto: SendLobbyStateToClientsDTO) {
        sessions.forEach { sesh ->
            dto.yourId = sesh.id
            sendToSessionById(sesh.id, convertDTOtoJSON(dto))
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

    fun convertDTOtoJSON(dto: DTO): String{
        return Gson().toJson(dto)
    }

    abstract fun onClose(session: WsSession, status: Int, message: String?)
    abstract fun onMessage(session: WsSession, message: String)
    abstract fun onConnect(session: WsSession)

}