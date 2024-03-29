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
    val fireBalls = mutableListOf<FireBall>()
    val powerUps = mutableListOf<IPowerUp>()
    val homingBalls = mutableListOf<HomingBall>()
    val gameStateLock = Object()

    override fun tick() {
        players.forEach { it.tick() }
        fireBalls.forEach { it.tick() }
        homingBalls.forEach { it.tick() }
        powerUpSpawner.tick()
        proxy.sendGameStateToClients()
    }

    fun initializeGameState(lobbyPlayers: MutableSet<main.kotlin.lobby.Player>){
        synchronized(gameStateLock){
            lobbyPlayers.forEach {lobbyPlayer ->
                Player(lobbyPlayer.id, lobbyPlayer.name,100 + players.size * 75, 500, this).also { player ->
                    players.add(player)
                }
            }
            fireBalls.add(FireBall(200, 300, FireBall.MovementDirection.DOWN_RIGHT, this))
            fireBalls.add(FireBall(400, 200, FireBall.MovementDirection.UP_RIGHT, this))
            fireBalls.add(FireBall(700, 400, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(800, 100, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(800, 400, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(500, 400, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(600, 400, FireBall.MovementDirection.DOWN_RIGHT, this))
            fireBalls.add(FireBall(600, 600, FireBall.MovementDirection.DOWN_LEFT, this))

            proxy.buildSendGameStateDTO().also { GameStatePublisher.broadcast(it) }
        }
    }

}

