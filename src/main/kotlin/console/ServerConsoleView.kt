package console

import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import main.kotlin.game.GameState
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO
import tornadofx.*

class ServerConsoleView : View("ServerConsole"), INetworkNewsPaperSubscriber {

    override val root: VBox by fxml("/fxml/ServerConsole.fxml")

    private val buttonSend: Button by fxid("sendButton")
    private val textAreaStreamIN: TextArea by fxid("textAreaStreamIN")

    private val  gameState = GameState()

    init {
        NetworkNewsPaper.getInstance().subscribe(this)

        buttonSend.setOnMouseClicked { mouseEvent ->
            if(mouseEvent.button == MouseButton.PRIMARY){
                println("send button clicked")
            }
        }
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
        }
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        textAreaStreamIN.text += "${dto.id} is now connected."
    }

}



