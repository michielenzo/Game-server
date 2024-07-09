package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.GameLoop
import main.kotlin.game.engine.Scheduler
import main.kotlin.game.spaceBalls.dto.GameConfigToClientsDTO
import main.kotlin.game.spaceBalls.dto.MeteoriteDirectionDTO
import main.kotlin.game.spaceBalls.gameobjects.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.PowerUp
import main.kotlin.publisher.MsgType
import main.kotlin.publisher.gamestate.GamePublisher
import java.util.concurrent.CopyOnWriteArrayList

class SpaceBalls: GameLoop(){

    private val proxy = GameProxy(this)
    private val powerUpSpawner = PowerUpSpawner(this)
    private val setupSpawner = SetupSpawner(this)

    private var isMultiplayerGame: Boolean = false
    private var winner: Player? = null

    var state: State = State.COUNTDOWN

    enum class State{
        COUNTDOWN,
        PLAYING
    }

    companion object {
        const val DIMENSION_WIDTH = 1100
        const val DIMENSION_HEIGHT = 650
        const val COUNTDOWN_MILLIS = 5000L
    }

    val players = CopyOnWriteArrayList<Player>()
    val meteorites = CopyOnWriteArrayList<Meteorite>()
    val powerUps = CopyOnWriteArrayList<PowerUp>()
    val homingBalls = CopyOnWriteArrayList<HomingBall>()
    val gameStateLock = Object()

    override fun startGame() {
        super.start()
        proxy.start()

        Scheduler.schedule(COUNTDOWN_MILLIS) {
            state = State.PLAYING
            fireEvent(GameEventType.METEORITES_UNFREEZE)
        }
    }

    override fun tick() {
        players.forEach { it.tick() }
        meteorites.forEach { it.tick() }
        homingBalls.forEach { it.tick() }
        powerUpSpawner.tick()

        if(isMultiplayerGame && winner == null && detectGameIsWon()) handleGameIsWon()

        if(detectEndOfGame()){ stopLoop() }
    }

    fun initializeGameState(roomPlayers: MutableSet<main.kotlin.room.Player>){
        synchronized(gameStateLock){
            setupSpawner.spawnObjects(roomPlayers, 8)

            isMultiplayerGame = players.size > 1

            val meteoritesDirectionInitDTO = meteorites.map {
                MeteoriteDirectionDTO(it.id, it.direction.toString())
            }

            GameConfigToClientsDTO(
                messageType = MsgType.GAME_CONFIG_TO_CLIENTS.value,
                powerUpWidth = PowerUp.WIDTH, powerUpHeight = PowerUp.HEIGHT,
                playerWidth = Player.WIDTH, playerHeight = Player.HEIGHT, playerSpeed = Player.SPEED,
                homingBallRadius = HomingBall.RADIUS, meteoriteDiameter = Meteorite.DIAMETER,
                countdownMillis = COUNTDOWN_MILLIS,
                meteoritesDirectionInit = meteoritesDirectionInitDTO
            ).also { GamePublisher.broadcast(it, players) }
        }
    }

    private fun handleGameIsWon() {
        winner = players.first { it.isAlive }

        winner?.let {
            fireEvent(GameEventType.WINNER_DECIDED, hashMapOf("playerName" to it.name))
        }
    }

    private fun detectGameIsWon(): Boolean = players.filter { it.isAlive }.toList().size == 1

    private fun detectEndOfGame(): Boolean = players.size == 0
}