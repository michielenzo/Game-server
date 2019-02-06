package console

import com.google.gson.Gson
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import main.kotlin.game.dto.SendGameStateDTO
import main.kotlin.lobby.dto.SendLobbyStateDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.gamestate.IGameStateNewsPaperSubscriber
import main.kotlin.newspaper.lobby.ILobbyNewsPaperSubscriber
import main.kotlin.newspaper.lobby.LobbyNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO
import tornadofx.*
import java.time.LocalDateTime

class ServerConsoleView : View("ServerConsole"), INetworkNewsPaperSubscriber, IGameStateNewsPaperSubscriber, ILobbyNewsPaperSubscriber {

    override val root: VBox by fxml("/fxml/ServerConsole.fxml")

    private val buttonSend: Button by fxid("sendButton")
    private val textAreaStreamIN: TextArea by fxid("textAreaStreamIN")

    init {
        NetworkNewsPaper.subscribe(this)
        GameStateNewsPaper.subscribe(this)
        LobbyNewsPaper.subscribe(this)

        buttonSend.setOnMouseClicked { mouseEvent ->
            if(mouseEvent.button == MouseButton.PRIMARY){
                println("send button clicked")
            }
        }
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectToServerMessage(dto)
        }
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is SendGameStateDTO -> handleSendGameStateMessage(dto)
        }
    }

    override fun notifyLobbyNews(dto: DTO) {
        when(dto){
            is SendLobbyStateDTO -> handleSendLobbyStateMessage(dto)
        }
    }

    private fun handleSendLobbyStateMessage(dto: SendLobbyStateDTO) {
        textAreaStreamIN.text += String()
                .plus(preFixOUT())
                .plus(Gson().toJson(dto))
                .plus("\n")
    }

    private fun handleSendGameStateMessage(dto: SendGameStateDTO) {
        textAreaStreamIN.text += String()
                .plus(preFixOUT())
                .plus(Gson().toJson(dto))
                .plus("\n")
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        textAreaStreamIN.text += String()
                .plus(preFixIN())
                .plus(dto.id)
                .plus(" is now connected.")
                .plus("\n")
    }

    private fun handleDisconnectToServerMessage(dto: DisconnectDTO) {
        textAreaStreamIN.text += String()
                .plus(preFixIN())
                .plus(dto.id)
                .plus(" has disconnected.")
                .plus("\n")
    }

    private fun preFixIN(): String{
        return String()
                .plus(LocalDateTime.now().hour)
                .plus(":")
                .plus(LocalDateTime.now().minute)
                .plus(":")
                .plus(LocalDateTime.now().second)
                .plus(" [IN] ")
    }

    private fun preFixOUT(): String{
        return String()
                .plus(LocalDateTime.now().hour)
                .plus(":")
                .plus(LocalDateTime.now().minute)
                .plus(":")
                .plus(LocalDateTime.now().second)
                .plus(" [OUT] ")
    }

}



