package main.kotlin.lobby

import main.kotlin.lobby.dto.LobbyStateDTO
import main.kotlin.lobby.dto.PlayerDTO
import main.kotlin.lobby.dto.SendLobbyStateToClientsDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.lobby.LobbyNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class Lobby: INetworkNewsPaperSubscriber {

    private val players = mutableListOf<Player>()

    private val lobbyStateLock = Object()

    init {
        NetworkNewsPaper.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectToServerMessage(dto)
        }
    }

    private fun handleDisconnectToServerMessage(dto: DisconnectDTO) {
        synchronized(lobbyStateLock){
            players.removeAll { it.id == dto.id }
            buildSendLobbyStateDTO().also { LobbyNewsPaper.broadcast(it) }
        }
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        synchronized(lobbyStateLock){
            Player(dto.id).also {
                players.add(it)
                LobbyNewsPaper.broadcast(buildSendLobbyStateDTO())
            }
        }
    }

    private fun buildSendLobbyStateDTO(): DTO {
        return SendLobbyStateToClientsDTO(LobbyStateDTO().also { lobbyStateDTO ->
            players.forEach { player ->
                PlayerDTO(player.id).also { playerDTO ->
                    lobbyStateDTO.players.add(playerDTO)
                }
            }
        })
    }

}