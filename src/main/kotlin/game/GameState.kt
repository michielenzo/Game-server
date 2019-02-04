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

    private val gameStateNewsPaper = GameStateNewsPaper.getInstance()

    private val connectToServerMessageLock = Object()

    init {
        NetworkNewsPaper.getInstance().subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
        }
    }

    private fun handleConnectToServerMessage(connectionDTO: ConnectionDTO) {
        Player(connectionDTO.id, 0, 0).also {player ->
            synchronized(connectToServerMessageLock){
                players.add(player)
                buildSendGameStateDTO().also { gameStateNewsPaper.broadcast(it) }
            }
        }
    }

    private fun buildSendGameStateDTO(): SendGameStateDTO{
        return SendGameStateDTO(GameStateDTO().also {gameStateDTO ->
            players.forEach{player ->
                PlayerDTO(player.sessionId, player.xPosition, player.yPosition).also { playerDTO ->
                    gameStateDTO.players.add(playerDTO)
                }
            }
        })
    }

}


