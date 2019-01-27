import console.ServerConsoleView
import javafx.stage.Stage
import network.PlayerWebsocket
import tornadofx.*


class Application : App(ServerConsoleView::class){

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
