package main.kotlin.game

import main.kotlin.console.dto.ContinueGameLoopDTO
import main.kotlin.console.dto.PauseGameLoopDTO
import main.kotlin.console.dto.StopGameLoopDTO
import main.kotlin.game.dto.*
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
            is BackToLobbyToServerDTO -> handleBackToLobbyToServerMessage(dto)
        }
    }

    override fun notifyConsoleNews(dto: DTO) {
        when(dto){
            is StopGameLoopDTO -> gameState.stopLoop()
            is PauseGameLoopDTO -> gameState.pauseGame()
            is ContinueGameLoopDTO -> gameState.continueGame()
        }
    }

    private fun handleBackToLobbyToServerMessage(dto: BackToLobbyToServerDTO) {
        synchronized(gameState){
            gameState.players.find { pl -> pl.sessionId == dto.playerId }.also {
                it?: return
                gameState.players.remove(it)
            }
            if(gameState.players.isEmpty()) {
                gameState.stopLoop()
            }
        }
    }

    fun sendGameStateToClients(){
        buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
    }

    private fun handleSendInputStateToServerMessage(dto: SendInputStateToServerDTO) {
        gameState.players.find { it.sessionId == dto.sessionId }.apply {
            this?: return
            wKey = dto.wKey
            aKey = dto.aKey
            sKey = dto.sKey
            dKey = dto.dKey
        }
    }

    private fun handleConnectToServerMessage(connectionDTO: ConnectionDTO) {
        synchronized(gameState.gameStateLock){
            Player(connectionDTO.id, connectionDTO.id,100 + gameState.players.size * 75, 100).also { player ->
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
            gameState.players.forEach{ player ->
                PlayerDTO(player.sessionId, player.name, player.xPosition, player.yPosition, player.health).also { playerDTO ->
                    gameStateDTO.players.add(playerDTO)
                }
            }
            gameState.fireBalls.forEach { fireBall ->
                FireBallDTO(fireBall.xPosition, fireBall.yPosition, fireBall.diameter).also { fireBallDTO ->
                    gameStateDTO.fireBalls.add(fireBallDTO)
                }
            }
        })
    }

}
