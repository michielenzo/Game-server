import network.PlayerWebsocket
import tornadofx.*


open class Application : App(){

    private fun setupNetwork(){
        PlayerWebsocket().also {
            it.initialize()
        }
    }

}
