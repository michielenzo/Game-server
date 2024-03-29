package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.dto.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.publisher.gamestate.GameStatePublisher
import main.kotlin.publisher.network.INetworkSubscriber
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.utilities.DTO

class GameProxy(private val spaceBalls: SpaceBalls): INetworkSubscriber{

    init {
        NetworkPublisher.subscriberQueue.add(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is DisconnectDTO -> handleDisconnectFromServerMessage(dto)
            is SendInputStateToServerDTO -> handleSendInputStateToServerMessage(dto)
            is BackToLobbyToServerDTO -> handleBackToLobbyToServerMessage(dto)
        }
    }

    private fun handleBackToLobbyToServerMessage(dto: BackToLobbyToServerDTO) {
        synchronized(spaceBalls){
            spaceBalls.players.find { pl -> pl.sessionId == dto.playerId }.also {
                it?: return
                spaceBalls.players.remove(it)
            }
            if(spaceBalls.players.isEmpty()) {
                spaceBalls.stopLoop()
            }
        }
    }

    fun sendGameStateToClients(){
        buildSendGameStateDTO().also { GameStatePublisher.broadcast(it) }
    }

    private fun handleSendInputStateToServerMessage(dto: SendInputStateToServerDTO) {
        spaceBalls.players.find { it.sessionId == dto.sessionId }.apply {
            this?: return
            wKey = dto.wKey
            aKey = dto.aKey
            sKey = dto.sKey
            dKey = dto.dKey
        }
    }

    private fun handleDisconnectFromServerMessage(dto: DisconnectDTO) {
        synchronized(spaceBalls.gameStateLock){
            spaceBalls.players.find { pl -> pl.sessionId == dto.id }.also {
                spaceBalls.players.remove(it)
            }
        }
    }

    @Synchronized fun buildSendGameStateDTO(): SendSpaceBallsGameStateToClientsDTO {
        return SendSpaceBallsGameStateToClientsDTO(GameStateDTO().also { gameStateDTO ->
            spaceBalls.players.forEach{ player ->
                PlayerDTO(
                    player.sessionId,
                    player.name,
                    player.xPosition,
                    player.yPosition,
                    player.health,
                    player.hasShield,
                    player.controlsInverted).also{ playerDTO ->
                        gameStateDTO.players.add(playerDTO)
                }
            }
            spaceBalls.fireBalls.forEach { fireBall ->
                FireBallDTO(fireBall.xPosition, fireBall.yPosition).also { fireBallDTO ->
                    gameStateDTO.fireBalls.add(fireBallDTO)
                }
            }
            spaceBalls.powerUps.forEach { powerUp ->
                when (powerUp) {
                    is MedKit -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, IPowerUp.PowerUpType.MED_KIT))
                    is Shield -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, IPowerUp.PowerUpType.SHIELD))
                    is Inverter -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, IPowerUp.PowerUpType.INVERTER))
                    is ControlInverter ->
                        gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, IPowerUp.PowerUpType.CONTROL_INVERTER))
                }
            }
            spaceBalls.homingBalls.forEach { homingBall ->
                gameStateDTO.homingBalls.add(HomingBallDTO(homingBall.xPosition, homingBall.yPosition))
            }
        })
    }

    private fun buildPowerUpDTO(powerUp: IPowerUp, powerUpType: IPowerUp.PowerUpType): PowerUpDTO {
        return PowerUpDTO(powerUpType.text, powerUp.xPosition, powerUp.yPosition)
    }
}
