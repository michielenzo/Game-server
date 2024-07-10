package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.dto.*
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.HomingBall
import main.kotlin.game.spaceBalls.gameobjects.Meteorite
import main.kotlin.game.spaceBalls.gameobjects.Player
import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.publisher.MsgType
import main.kotlin.publisher.gamestate.GamePublisher
import main.kotlin.publisher.network.INetworkSubscriber
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.utilities.DTO

class GameProxy(private val game: SpaceBalls): Thread(), INetworkSubscriber{

    private val messageRate = 30
    private val millisPerSecond = 1000.0
    private val millisPerMessage = millisPerSecond / messageRate

    init {
        NetworkPublisher.subscriberQueue.add(this)
    }

    override fun run() {
        val startTime = System.currentTimeMillis()
        var loops = 0
        while (!game.gameOver){
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

    fun sendConfigToClients() {
        val meteoritesDirectionInitDTO = game.meteorites.map {
            MeteoriteDirectionDTO(it.id, it.direction.toString())
        }

        GameConfigToClientsDTO(
            messageType = MsgType.GAME_CONFIG_TO_CLIENTS.value,
            powerUpWidth = PowerUp.WIDTH, powerUpHeight = PowerUp.HEIGHT,
            playerWidth = Player.WIDTH, playerHeight = Player.HEIGHT, playerSpeed = Player.SPEED,
            homingBallRadius = HomingBall.RADIUS, meteoriteDiameter = Meteorite.DIAMETER,
            countdownMillis = SpaceBalls.COUNTDOWN_MILLIS,
            meteoritesDirectionInit = meteoritesDirectionInitDTO
        ).also { GamePublisher.broadcast(it, game.players) }
    }

    private fun handleBackToRoomToServerMessage(dto: BackToRoomToServerDTO) {
        synchronized(game){
            game.players.find { pl -> pl.sessionId == dto.playerId }.also {
                it?: return
                game.players.remove(it)
            }
            if(game.players.isEmpty()) {
                game.stopLoop()
            }
        }
    }

    private fun handleSendInputStateToServerMessage(dto: SendInputStateToServerDTO) {
        game.players.find { it.sessionId == dto.sessionId }.apply {
            this?: return
            wKey = dto.wKey
            aKey = dto.aKey
            sKey = dto.sKey
            dKey = dto.dKey
        }
    }

    private fun handleDisconnectFromServerMessage(dto: DisconnectDTO) {
        synchronized(game.gameStateLock){
            game.players.find { pl -> pl.sessionId == dto.id }.also {
                game.players.remove(it)
            }
        }
    }

    @Synchronized fun buildSendGameStateDTO(): SendSpaceBallsGameStateToClientsDTO {
        return SendSpaceBallsGameStateToClientsDTO(GameStateDTO().also { gameStateDTO ->
            game.players.forEach{ player ->
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
            game.meteorites.forEach { meteorite ->
                MeteoriteDTO(meteorite.id, meteorite.xPos, meteorite.yPos).also { meteoriteDTO ->
                    gameStateDTO.meteorites.add(meteoriteDTO)
                }
            }
            game.powerUps.forEach { powerUp ->
                when (powerUp) {
                    is MedKit -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.Type.MED_KIT))
                    is Shield -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.Type.SHIELD))
                    is Inverter -> gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.Type.INVERTER))
                    is ControlInverter ->
                        gameStateDTO.powerUps.add(buildPowerUpDTO(powerUp, PowerUp.Type.CONTROL_INVERTER))
                }
            }
            game.homingBalls.forEach { homingBall ->
                gameStateDTO.homingBalls.add(HomingBallDTO(homingBall.id, homingBall.xPos, homingBall.yPos))
            }
            game.gameEvents.forEach { event ->
                gameStateDTO.events.add(GameEventDTO(event.type, event.data))
            }
            game.gameEvents.clear()
        })
    }

    private fun buildPowerUpDTO(powerUp: GameObject, powerUpType: PowerUp.Type): PowerUpDTO {
        return PowerUpDTO(powerUpType.text, powerUp.xPos, powerUp.yPos)
    }
}
