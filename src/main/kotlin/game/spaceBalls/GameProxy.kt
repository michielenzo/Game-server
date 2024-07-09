package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.dto.*
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.publisher.gamestate.GamePublisher
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
                buildSendGameStateDTO().also { GamePublisher.broadcast(it) }
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
                    player.xPos,
                    player.yPos,
                    player.health,
                    player.hasShield,
                    player.controlsInverted).also{ playerDTO ->
                        gameStateDTO.players.add(playerDTO)
                }
            }
            spaceBalls.meteorites.forEach { meteorite ->
                MeteoriteDTO(meteorite.id, meteorite.xPos, meteorite.yPos).also { meteoriteDTO ->
                    gameStateDTO.meteorites.add(meteoriteDTO)
                }
            }
            spaceBalls.powerUps.forEach { powerUp ->
                when (powerUp) {
                    is MedKit -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.PowerUpType.MED_KIT))
                    is Shield -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.PowerUpType.SHIELD))
                    is Inverter -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.PowerUpType.INVERTER))
                    is ControlInverter ->
                        gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.PowerUpType.CONTROL_INVERTER))
                }
            }
            spaceBalls.homingBalls.forEach { homingBall ->
                gameStateDTO.homingBalls.add(HomingBallDTO(homingBall.id, homingBall.xPos, homingBall.yPos))
            }
            spaceBalls.gameEvents.forEach { event ->
                gameStateDTO.events.add(GameEventDTO(event.type, event.data))
            }
            spaceBalls.gameEvents.clear()
        })
    }

    private fun buildPowerUpDTO(powerUp: GameObject, powerUpType: PowerUp.PowerUpType): PowerUpDTO {
        return PowerUpDTO(powerUpType.text, powerUp.xPos, powerUp.yPos)
    }
}
