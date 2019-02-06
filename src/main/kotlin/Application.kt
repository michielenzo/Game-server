import console.ServerConsoleView
import javafx.stage.Stage
import main.kotlin.game.GameState
import network.PlayerWebsocket
import tornadofx.*


class Application : App(ServerConsoleView::class){

    private val gameState = GameState()

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
