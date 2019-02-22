package main.kotlin.game

import main.kotlin.game.gameobject.FireBall
import main.kotlin.game.gameobject.Player
import main.kotlin.newspaper.gamestate.GameStateNewsPaper

open class GameState: GameLoop(){

    private val proxy = GameProxy(this)

    companion object {
        const val DIMENSION_WIDTH = 1100
        const val DIMENSION_HEIGHT = 700
    }

    val players = mutableListOf<Player>()
    val fireBalls = mutableListOf<FireBall>()
    val gameStateLock = Object()

    override fun tick() {
        players.forEach { it.tick() }
        fireBalls.forEach { it.tick() }
        proxy.sendGameStateToClients()
    }

    fun initializeGameState(lobbyPlayers: MutableList<main.kotlin.lobby.Player>){
        synchronized(gameStateLock){
            lobbyPlayers.forEach {lobbyPlayer ->
                Player(lobbyPlayer.id, lobbyPlayer.name,100 + players.size * 75, 500).also { player ->
                    players.add(player)
                }
            }
            fireBalls.add(FireBall(200, 300, FireBall.MovementDirection.DOWN_RIGHT, this))
            fireBalls.add(FireBall(400, 200, FireBall.MovementDirection.UP_RIGHT, this))
            fireBalls.add(FireBall(700, 400, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(800, 100, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(800, 400, FireBall.MovementDirection.UP_LEFT, this))
            fireBalls.add(FireBall(500, 400, FireBall.MovementDirection.UP_LEFT, this))
            proxy.buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
        }

    }

}

