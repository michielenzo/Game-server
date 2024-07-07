package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.GameEvent
import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.GameLoop
import main.kotlin.game.engine.Scheduler
import main.kotlin.game.spaceBalls.dto.GameConfigToClientsDTO
import main.kotlin.game.spaceBalls.dto.MeteoriteDirectionDTO
import main.kotlin.game.spaceBalls.gameobjects.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.IPowerUp
import main.kotlin.publisher.MsgType
import main.kotlin.publisher.gamestate.GamePublisher
import java.util.concurrent.CopyOnWriteArrayList

class SpaceBalls: GameLoop(){

    private val proxy = GameProxy(this)
    private val powerUpSpawner = PowerUpSpawner(this)

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
    val powerUps = CopyOnWriteArrayList<IPowerUp>()
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
            roomPlayers.forEach {roomPlayer ->
                Player(roomPlayer.id, roomPlayer.name,100.0 + players.size * 75.0, 500.0, this).also { player ->
                    players.add(player)
                }
            }

            isMultiplayerGame = players.size > 1

            meteorites.add(Meteorite(200.0, 300.0, Meteorite.MovementDirection.DOWN_RIGHT, this))
            meteorites.add(Meteorite(400.0, 200.0, Meteorite.MovementDirection.UP_RIGHT, this))
            meteorites.add(Meteorite(700.0, 400.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(800.0, 100.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(1000.0, 200.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(500.0, 300.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(600.0, 400.0, Meteorite.MovementDirection.DOWN_RIGHT, this))
            meteorites.add(Meteorite(850.0, 500.0, Meteorite.MovementDirection.DOWN_LEFT, this))

            val meteoritesDirectionInitDTO = meteorites.map {
                MeteoriteDirectionDTO(it.id, it.direction.toString())
            }

            GameConfigToClientsDTO(
                messageType = MsgType.GAME_CONFIG_TO_CLIENTS.value,
                powerUpWidth = IPowerUp.WIDTH, powerUpHeight = IPowerUp.HEIGHT,
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