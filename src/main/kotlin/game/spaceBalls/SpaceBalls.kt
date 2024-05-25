package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.GameLoop
import main.kotlin.game.spaceBalls.gameobjects.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.IPowerUp
import main.kotlin.publisher.gamestate.GameStatePublisher

class SpaceBalls: GameLoop(){

    private val proxy = GameProxy(this)
    private val powerUpSpawner = PowerUpSpawner(this)

    companion object {
        const val DIMENSION_WIDTH = 1100
        const val DIMENSION_HEIGHT = 650
    }

    val players = mutableListOf<Player>()
    val meteorites = mutableListOf<Meteorite>()
    val powerUps = mutableListOf<IPowerUp>()
    val homingBalls = mutableListOf<HomingBall>()
    val gameStateLock = Object()

    override fun startGame() {
        super.start()
        proxy.start()
    }

    override fun tick() {
        players.forEach { it.tick() }
        meteorites.forEach { it.tick() }
        homingBalls.forEach { it.tick() }
        powerUpSpawner.tick()

        if(detectEndOfGame()){ stopLoop() }
    }

    fun initializeGameState(roomPlayers: MutableSet<main.kotlin.room.Player>){
        synchronized(gameStateLock){
            roomPlayers.forEach {roomPlayer ->
                Player(roomPlayer.id, roomPlayer.name,100.0 + players.size * 75.0, 500.0, this).also { player ->
                    players.add(player)
                }
            }
            meteorites.add(Meteorite(200.0, 300.0, Meteorite.MovementDirection.DOWN_RIGHT, this))
            meteorites.add(Meteorite(400.0, 200.0, Meteorite.MovementDirection.UP_RIGHT, this))
            meteorites.add(Meteorite(700.0, 400.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(800.0, 100.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(800.0, 400.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(500.0, 400.0, Meteorite.MovementDirection.UP_LEFT, this))
            meteorites.add(Meteorite(600.0, 400.0, Meteorite.MovementDirection.DOWN_RIGHT, this))
            meteorites.add(Meteorite(600.0, 600.0, Meteorite.MovementDirection.DOWN_LEFT, this))
        }
    }

    private fun detectEndOfGame(): Boolean {
        return players.size == 0
    }
}

