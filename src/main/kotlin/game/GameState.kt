package main.kotlin.game

import main.kotlin.game.dto.GameStateDTO
import main.kotlin.game.dto.PlayerDTO
import main.kotlin.game.dto.SendGameStateDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class GameState : INetworkNewsPaperSubscriber {

    private val players = mutableListOf<Player>()

    private val connectToServerMessageLock = Object()

    init {
        NetworkNewsPaper.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
        }
    }

    private fun handleConnectToServerMessage(connectionDTO: ConnectionDTO) {
        println("1")
        synchronized(connectToServerMessageLock){
            Player(connectionDTO.id, 10 + players.size * 75, 10).also {player ->
                players.add(player)
                println("2")
                buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
            }
        }
    }

    private fun buildSendGameStateDTO(): SendGameStateDTO {
        return SendGameStateDTO(GameStateDTO().also {gameStateDTO ->
            println("3")
            players.forEach{player ->
                PlayerDTO(player.sessionId, player.xPosition, player.yPosition).also { playerDTO ->
                    println("4")
                    gameStateDTO.players.add(playerDTO)
                }
            }
        })
    }

}

