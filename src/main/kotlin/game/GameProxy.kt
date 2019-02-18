package main.kotlin.game

import main.kotlin.console.dto.ContinueGameLoopDTO
import main.kotlin.console.dto.PauseGameLoopDTO
import main.kotlin.console.dto.StopGameLoopDTO
import main.kotlin.game.dto.GameStateDTO
import main.kotlin.game.dto.PlayerDTO
import main.kotlin.game.dto.SendGameStateToClientsDTO
import main.kotlin.game.dto.SendInputStateToServerDTO
import main.kotlin.game.gameobject.Player
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.console.IConsoleNewsPaperSubscriber
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.ConsoleNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class GameProxy(private val gameState: GameState): INetworkNewsPaperSubscriber, IConsoleNewsPaperSubscriber {

    init {
        NetworkNewsPaper.subscriberQueue.add(this)
        ConsoleNewsPaper.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectFromServerMessage(dto)
            is SendInputStateToServerDTO -> handleSendInputStateToServerMessage(dto)
        }
    }

    override fun notifyConsoleNews(dto: DTO) {
        when(dto){
            is StopGameLoopDTO -> gameState.stopLoop()
            is PauseGameLoopDTO -> gameState.pauseGame()
            is ContinueGameLoopDTO -> gameState.continueGame()
        }
    }

    fun sendGameStateToClients(){
        buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
    }

    private fun handleSendInputStateToServerMessage(dto: SendInputStateToServerDTO) {
        gameState.players.find { it.sessionId == dto.sessionId }.apply {
            Player.InputState.wKey = dto.wKey
            Player.InputState.aKey = dto.aKey
            Player.InputState.sKey = dto.sKey
            Player.InputState.dKey = dto.dKey
        }
    }

    private fun handleConnectToServerMessage(connectionDTO: ConnectionDTO) {
        synchronized(gameState.gameStateLock){
            Player(connectionDTO.id, 10 + gameState.players.size * 75, 10).also { player ->
                gameState.players.add(player)
                buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
            }
        }
    }

    private fun handleDisconnectFromServerMessage(dto: DisconnectDTO) {
        synchronized(gameState.gameStateLock){
            gameState.players.removeAll { it.sessionId == dto.id }
            buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
        }
    }

    fun buildSendGameStateDTO(): SendGameStateToClientsDTO {
        return SendGameStateToClientsDTO(GameStateDTO().also { gameStateDTO ->
            gameState.players.forEach{player ->
                PlayerDTO(player.sessionId, player.xPosition, player.yPosition).also { playerDTO ->
                    gameStateDTO.players.add(playerDTO)
                }
            }
        })
    }

}
