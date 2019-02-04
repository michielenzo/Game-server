package main.kotlin.game

import main.kotlin.game.dto.NewPlayerDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class GameState : INetworkNewsPaperSubscriber {

    private var players = mutableListOf<Player>()

    private val gameStateNewsPaper = GameStateNewsPaper.getInstance()

    private val connectToServerMessageLock = Object()

    init {
        NetworkNewsPaper.getInstance().subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        println("hello")
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
        }
    }

    private fun handleConnectToServerMessage(connectionDTO: ConnectionDTO) {
        println("hello2")
        Player(connectionDTO.id, 0, 0).also {player ->
            synchronized(connectToServerMessageLock){
                players.add(player)
                NewPlayerDTO(player.sessionId, player.xPosition, player.yPosition).also {dto ->
                    println("hello3")
                    gameStateNewsPaper.broadcast(dto)
                }
            }
        }
    }

}
