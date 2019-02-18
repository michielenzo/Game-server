package main.kotlin.game

import main.kotlin.game.gameobject.Player
import main.kotlin.newspaper.gamestate.GameStateNewsPaper

open class GameState: GameLoop(){

    private val proxy = GameProxy(this)

    val players = mutableListOf<Player>()
    val gameStateLock = Object()

    override fun tick() {
        players.forEach { it.tick() }
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
    }

}

