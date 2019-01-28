package console

import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import main.kotlin.network.INetworkNewsPaperSubscriber
import main.kotlin.network.NetworkNewsPaper
import tornadofx.View

class ServerConsoleView : View("ServerConsole"), INetworkNewsPaperSubscriber {

    override val root: HBox by fxml("/fxml/ServerConsole.fxml")

    private val buttonSend: Button by fxid("sendButton")

    private val textAreaStreamIN: TextArea by fxid("textAreaStreamIN")

    init {
        NetworkNewsPaper.getInstance().subscribe(this)

        buttonSend.setOnMouseClicked { mouseEvent ->
            if(mouseEvent.button == MouseButton.PRIMARY){
                println("send button clicked")
            }
        }
    }

    override fun notifyNetworkNews(message: String) {
        textAreaStreamIN.text += "$message \n"
    }

}
