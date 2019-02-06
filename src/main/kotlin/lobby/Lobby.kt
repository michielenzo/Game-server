package main.kotlin.lobby

import main.kotlin.lobby.dto.LobbyStateDTO
import main.kotlin.lobby.dto.PlayerDTO
import main.kotlin.lobby.dto.SendLobbyStateDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.newspaper.lobby.LobbyNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class Lobby: INetworkNewsPaperSubscriber {

    private val players = mutableListOf<Player>()

    private val connectToServerMessageLock = Object()

    init {
        NetworkNewsPaper.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
        }
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        synchronized(connectToServerMessageLock){
            Player(dto.id).also {
                players.add(it)
                LobbyNewsPaper.broadcast(buildSendLobbyStateDTO())
            }
        }
    }

    private fun buildSendLobbyStateDTO(): DTO {
        return SendLobbyStateDTO(LobbyStateDTO().also { lobbyStateDTO ->
            players.forEach { player ->
                PlayerDTO(player.id).also { playerDTO ->
                    lobbyStateDTO.players.add(playerDTO)
                }
            }
        })
    }

}