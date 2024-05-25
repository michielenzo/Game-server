package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.dto.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.publisher.gamestate.GameStatePublisher
import main.kotlin.publisher.network.INetworkSubscriber
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.utilities.DTO

class GameProxy(private val spaceBalls: SpaceBalls): Thread(), INetworkSubscriber{

    private val messageRate = 30
    private val millisPerSecond = 1000.0
    private val millisPerMessage = millisPerSecond / messageRate

    init {
        NetworkPublisher.subscriberQueue.add(this)
    }

    override fun run() {
        val startTime = System.currentTimeMillis()
        var loops = 0
        while (!spaceBalls.gameOver){
            val delta = (System.currentTimeMillis() - startTime) - (loops * millisPerMessage)
            if(delta >= millisPerMessage){
                buildSendGameStateDTO().also { GameStatePublisher.broadcast(it) }
                loops++
            }
        }
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is DisconnectDTO -> handleDisconnectFromServerMessage(dto)
            is SendInputStateToServerDTO -> handleSendInputStateToServerMessage(dto)
            is BackToRoomToServerDTO -> handleBackToRoomToServerMessage(dto)
        }
    }

    private fun handleBackToRoomToServerMessage(dto: BackToRoomToServerDTO) {
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
                    player.id,
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
            spaceBalls.meteorites.forEach { meteorite ->
                MeteoriteDTO(meteorite.id, meteorite.xPosition, meteorite.yPosition).also { meteoriteDTO ->
                    gameStateDTO.meteorites.add(meteoriteDTO)
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
                gameStateDTO.homingBalls.add(HomingBallDTO(homingBall.id, homingBall.xPosition, homingBall.yPosition))
            }
        })
    }

    private fun buildPowerUpDTO(powerUp: IPowerUp, powerUpType: IPowerUp.PowerUpType): PowerUpDTO {
        return PowerUpDTO(powerUpType.text, powerUp.xPosition, powerUp.yPosition)
    }
}
