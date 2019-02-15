import console.ServerConsoleView
import javafx.stage.Stage
import main.kotlin.game.GameState
import main.kotlin.lobby.Lobby
import network.PlayerWebsocket
import tornadofx.*


class Application : App(ServerConsoleView::class){

    private val lobby = Lobby()

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }

    init {
        PlayerWebsocket().also {
            it.initialize()
        }
    }

}
