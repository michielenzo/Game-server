package console

import com.google.gson.Gson
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import main.kotlin.console.dto.ContinueGameLoopDTO
import main.kotlin.console.dto.PauseGameLoopDTO
import main.kotlin.console.dto.StopGameLoopDTO
import main.kotlin.game.dto.SendGameStateToClientsDTO
import main.kotlin.game.dto.SendInputStateToServerDTO
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.lobby.dto.StartGameToServerDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.gamestate.IGameStateNewsPaperSubscriber
import main.kotlin.newspaper.lobby.ILobbyNewsPaperSubscriber
import main.kotlin.newspaper.lobby.LobbyNewsPaper
import main.kotlin.newspaper.network.ConsoleNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO
import tornadofx.*
import java.time.LocalDateTime

class ServerConsoleView : View("ServerConsole"), INetworkNewsPaperSubscriber, IGameStateNewsPaperSubscriber, ILobbyNewsPaperSubscriber {

    override val root: VBox by fxml("/fxml/ServerConsole.fxml")

    private val buttonSend: Button by fxid("sendButton")
    private val textAreaStreamIN: TextArea by fxid("textAreaStreamIN")
    private val commandField: TextField by fxid("commandField")

    init {
        NetworkNewsPaper.subscribe(this)
        GameStateNewsPaper.subscribe(this)
        LobbyNewsPaper.subscribe(this)

        buttonSend.setOnMouseClicked { mouseEvent ->
            if(mouseEvent.button == MouseButton.PRIMARY){
                if (commandField.text == "stop"){
                    ConsoleNewsPaper.broadcast(StopGameLoopDTO())
                }else if (commandField.text == "pause"){
                    ConsoleNewsPaper.broadcast(PauseGameLoopDTO())
                }else if (commandField.text == "continue"){
                    ConsoleNewsPaper.broadcast(ContinueGameLoopDTO())
                }else if (commandField.text == "clear"){
                    textAreaStreamIN.text = ""
                }
                commandField.text = ""
            }
        }
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectToServerMessage(dto)
            is StartGameToServerDTO -> handleStartGameToServerDTO(dto)
            is SendInputStateToServerDTO -> handleSendInputStateToServerMessage(dto)
        }
    }

    override fun notifyGameStateNews(dto: DTO) {
        when(dto){
            is SendGameStateToClientsDTO -> handleSendGameStateMessage(dto)
        }
    }

    override fun notifyLobbyNews(dto: DTO) {
        when(dto){
            is SendLobbyStateToClientsDTO -> handleSendLobbyStateMessage(dto)
        }
    }

    private fun handleSendInputStateToServerMessage(dto: SendInputStateToServerDTO) {
        textAreaStreamIN.text += preFixIN()
                .plus(Gson().toJson(dto))
                .plus("\n")
    }

    private fun handleStartGameToServerDTO(dto: StartGameToServerDTO) {
        textAreaStreamIN.text += preFixIN()
                .plus(Gson().toJson(dto))
                .plus("\n")
    }

    private fun handleSendLobbyStateMessage(dto: SendLobbyStateToClientsDTO) {
        textAreaStreamIN.text += preFixOUT()
                .plus(Gson().toJson(dto))
                .plus("\n")
    }

    private fun handleSendGameStateMessage(dto: SendGameStateToClientsDTO) {
        textAreaStreamIN.text += preFixOUT()
                .plus(Gson().toJson(dto))
                .plus("\n")
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        textAreaStreamIN.text += preFixIN()
                .plus(dto.id)
                .plus(" is now connected.")
                .plus("\n")
    }

    private fun handleDisconnectToServerMessage(dto: DisconnectDTO) {
        textAreaStreamIN.text += preFixIN()
                .plus(dto.id)
                .plus(" has disconnected.")
                .plus("\n")
    }

    private fun preFixIN(): String{
        return LocalDateTime.now().hour.toString()
                .plus(":")
                .plus(LocalDateTime.now().minute)
                .plus(":")
                .plus(LocalDateTime.now().second)
                .plus(" [IN] ")
    }

    private fun preFixOUT(): String{
        return LocalDateTime.now().hour.toString()
                .plus(":")
                .plus(LocalDateTime.now().minute)
                .plus(":")
                .plus(LocalDateTime.now().second)
                .plus(" [OUT] ")
    }

}



