package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.GameLoop
import main.kotlin.game.engine.Scheduler
import main.kotlin.game.spaceBalls.gameobjects.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.PowerUp
import main.kotlin.game.spaceBalls.spawners.MeteoriteSpawner
import main.kotlin.game.spaceBalls.spawners.PowerUpSpawner
import main.kotlin.game.spaceBalls.spawners.SetupSpawner
import java.util.concurrent.CopyOnWriteArrayList

class SpaceBalls: GameLoop(){

    private val proxy = GameProxy(this)
    private val powerUpSpawner = PowerUpSpawner(this)
    private val setupSpawner = SetupSpawner(this)
    private val meteoriteSpawner = MeteoriteSpawner(this)

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
            meteoriteSpawner.start()
        }
    }

    override fun tick() {
        players.forEach { it.tick() }
        meteorites.forEach { it.tick() }
        homingBalls.forEach { it.tick() }
        powerUpSpawner.tick()
        meteoriteSpawner.tick()

        if(isMultiplayerGame && winner == null && detectGameIsWon()) handleGameIsWon()

        if(detectEndOfGame()){ stopLoop() }
    }

    fun initializeGameState(roomPlayers: MutableSet<main.kotlin.room.Player>){
        synchronized(gameStateLock){
            setupSpawner.spawnObjects(roomPlayers, MeteoriteSpawner.START_AMOUNT)

            isMultiplayerGame = players.size > 1

            proxy.sendConfigToClients()
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

    override fun getPlayers(): List<Player> = players
}