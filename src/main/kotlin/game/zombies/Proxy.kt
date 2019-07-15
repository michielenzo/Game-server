package main.kotlin.game.zombies

import main.kotlin.game.zombies.dto.GameStateDTO
import main.kotlin.game.zombies.dto.PlayerDTO
import main.kotlin.game.zombies.dto.SendZombiesGameStateToClientsDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.utilities.DTO

class Proxy(val zombies: Zombies): INetworkNewsPaperSubscriber {

    override fun notifyNetworkNews(dto: DTO) {

    }

    fun sendGameStateToClients() {
        buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
    }

    private fun buildSendGameStateDTO(): SendZombiesGameStateToClientsDTO {
        return SendZombiesGameStateToClientsDTO(GameStateDTO(mutableListOf<PlayerDTO>().also { playersDTO ->
           zombies.players.forEach { player ->
               playersDTO.add(PlayerDTO(player.sessionId, player.name, player.xPos, player.yPos))
           }
        }))
    }
}