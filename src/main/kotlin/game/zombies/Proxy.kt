package main.kotlin.game.zombies

import main.kotlin.game.spaceBalls.dto.SendInputStateToServerDTO
import main.kotlin.game.zombies.dto.GameStateDTO
import main.kotlin.game.zombies.dto.PlayerDTO
import main.kotlin.game.zombies.dto.SendZombiesGameStateToClientsDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class Proxy(val zombies: Zombies): INetworkNewsPaperSubscriber {

    init {
        NetworkNewsPaper.subscriberQueue.add(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is SendInputStateToServerDTO -> handleNewInputState(dto)
        }
    }

    private fun handleNewInputState(dto: SendInputStateToServerDTO) {
        zombies.players.find { it.sessionId == dto.sessionId }.apply {
            this?: return
            wKey = dto.wKey
            aKey = dto.aKey
            sKey = dto.sKey
            dKey = dto.dKey
        }
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