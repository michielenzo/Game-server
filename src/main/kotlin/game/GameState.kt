package main.kotlin.game

import main.kotlin.newspaper.gamestate.GameStateNewsPaper

open class GameState{

    private val proxy = GameProxy(this)

    val players = mutableListOf<Player>()
    val gameStateLock = Object()

    fun initializeGameState(lobbyPlayers: MutableList<main.kotlin.lobby.Player>){
        synchronized(gameStateLock){
            lobbyPlayers.forEach {lobbyPlayer ->
                main.kotlin.game.Player(lobbyPlayer.id, 10 + players.size * 75, 10).also {player ->
                    players.add(player)
                }
            }
            proxy.buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
        }
    }

}

