package main.kotlin.game.zombies

import main.kotlin.game.engine.GameLoop
import main.kotlin.game.zombies.gameObjects.Player

class Zombies: GameLoop() {

    private val proxy = Proxy(this)

    val players = mutableListOf<Player>()

    override fun tick() {
        players.forEach { it.tick() }
        proxy.sendGameStateToClients()
    }

    fun initializeGameState(roomPlayers: MutableSet<main.kotlin.room.Player>){
        roomPlayers.forEach {roomPlayer ->
            Player(roomPlayer.id, roomPlayer.name, 100 + 100*players.size, 500).also { player ->
                players.add(player)
            }
        }
    }
}