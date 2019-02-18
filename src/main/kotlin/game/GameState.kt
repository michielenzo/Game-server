package main.kotlin.game

import main.kotlin.game.gameobject.FireBall
import main.kotlin.game.gameobject.Player
import main.kotlin.newspaper.gamestate.GameStateNewsPaper

open class GameState: GameLoop(){

    private val proxy = GameProxy(this)

    companion object {
        const val DIMENSION_WIDTH = 1000
        const val DIMENSION_HEIGHT = 600
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
                Player(lobbyPlayer.id, 10 + players.size * 75, 10).also { player ->
                    players.add(player)
                }
            }
            proxy.buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
        }
        fireBalls.add(FireBall(200, 300, FireBall.MovementDirection.DOWN_RIGHT))
        fireBalls.add(FireBall(400, 200, FireBall.MovementDirection.UP_RIGHT))
    }

}

