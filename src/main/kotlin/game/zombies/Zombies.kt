package main.kotlin.game.zombies

import main.kotlin.game.engine.GameLoop
import main.kotlin.game.zombies.gameObjects.Player

class Zombies: GameLoop() {

    private val proxy = Proxy(this)

    val players = mutableListOf<Player>()

    override fun tick() {
        proxy.sendGameStateToClients()
    }

    fun initializeGameState(lobbyPlayers: MutableSet<main.kotlin.lobby.Player>){
        lobbyPlayers.forEach {lobbyPlayer ->
            Player(lobbyPlayer.id, lobbyPlayer.name).also { player ->
                players.add(player)
            }
        }
    }
}