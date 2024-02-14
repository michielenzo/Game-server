package network

import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.community.ssl.SslPlugin
import io.javalin.community.ssl.TlsConfig
import io.javalin.websocket.WsContext
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.utilities.DTO

abstract class Websocket {

    protected val sessions = mutableListOf<WsContext>()

    fun initialize() {
        val deploymentMode = System.getenv("DEPLOYMENT")
        val endPointPath = System.getenv("WS_ENDPOINT_PATH")
        val port = System.getenv("WS_PORT").toIntOrNull()
            ?: throw IllegalArgumentException("The WS_PORT environment variable should be an integer.")

        val app: Javalin
        if(deploymentMode == "ssl-server"){
            val sslPlugin = SslPlugin { conf ->
                conf.pemFromPath("ssl/certificate.pem", "ssl/private.key")
                conf.securePort = port
                conf.http2 = false
                conf.tlsConfig = TlsConfig.MODERN
            }

            app = Javalin.create { config ->
                config.registerPlugin(sslPlugin)
            }.start(port)
        } else{
            app = Javalin.create().start(port)
        }

        app.ws(endPointPath) { ws ->
            ws.onConnect { ctx ->
                synchronized(this) {
                    sessions.add(ctx)
                }
                onConnect(ctx)
            }
            ws.onMessage { ctx ->
                onMessage(ctx, ctx.message())
            }
            ws.onClose { ctx ->
                synchronized(this) {
                    sessions.remove(ctx)
                }
                onClose(ctx, ctx.status(), ctx.reason())
            }
            ws.onError { ctx -> println(ctx.error()) }
        }
    }

    @Synchronized
    fun sendToAllSessions(message: String) {
        sessions.forEach { ctx ->
            if (ctx.session.isOpen) ctx.send(message)
        }
    }

    @Synchronized
    fun sendToAllSessionsAndSetClientId(dto: SendLobbyStateToClientsDTO) {
        sessions.forEach { ctx ->
            dto.yourId = ctx.sessionId()
            sendToSessionById(ctx.sessionId(), convertDTOtoJSON(dto))
        }
    }

    @Synchronized
    fun sendToSessionById(sessionId: String?, message: String) {
        sessions.find { ctx -> ctx.sessionId() == sessionId }?.let { wsCtx ->
            if (wsCtx.session.isOpen) wsCtx.send(message)
        }
    }

    @Synchronized
    fun sendToSessionSet(sessionSet: Set<WsContext>, message: String) {
        sessionSet.forEach { ctx ->
            if (ctx.session.isOpen) ctx.send(message)
        }
    }

    fun convertDTOtoJSON(dto: DTO): String {
        return Gson().toJson(dto)
    }

    abstract fun onClose(wsCtx: WsContext, status: Int, message: String?)
    abstract fun onMessage(wsCtx: WsContext, message: String)
    abstract fun onConnect(wsCtx: WsContext)
}
