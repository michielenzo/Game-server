package main.kotlin.game

import main.kotlin.console.dto.ContinueGameLoopDTO
import main.kotlin.console.dto.PauseGameLoopDTO
import main.kotlin.console.dto.StopGameLoopDTO
import main.kotlin.game.dto.*
import main.kotlin.game.gameobject.IPowerUp
import main.kotlin.game.gameobject.MedKit
import main.kotlin.game.gameobject.Shield
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

    private fun handleDisconnectFromServerMessage(dto: DisconnectDTO) {
        synchronized(gameState.gameStateLock){
            gameState.players.find { pl -> pl.sessionId == dto.id }.also {
                gameState.players.remove(it)
            }
        }
    }

    @Synchronized fun buildSendGameStateDTO(): SendGameStateToClientsDTO {
        return SendGameStateToClientsDTO(GameStateDTO().also { gameStateDTO ->
            gameState.players.forEach{ player ->
                PlayerDTO(player.sessionId, player.name, player.xPosition, player.yPosition, player.health, player.hasShield).also { playerDTO ->
                    gameStateDTO.players.add(playerDTO)
                }
            }
            gameState.fireBalls.forEach { fireBall ->
                FireBallDTO(fireBall.xPosition, fireBall.yPosition, fireBall.diameter).also { fireBallDTO ->
                    gameStateDTO.fireBalls.add(fireBallDTO)
                }
            }
            gameState.powerUps.forEach { powerUp ->
                if(powerUp is MedKit){
                    gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, IPowerUp.PowerUpType.MED_KIT))
                }else if(powerUp is Shield){
                    gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, IPowerUp.PowerUpType.SHIELD))
                }
            }
        })
    }

    private fun buildPowerUpDTO(powerUp: IPowerUp, powerUpType: IPowerUp.PowerUpType): PowerUpDTO {
        return PowerUpDTO(powerUpType.text,
                          powerUp.xPosition, powerUp.yPosition,
                          IPowerUp.WIDTH, IPowerUp.HEIGHT)
    }

}
