package main.kotlin.game

import main.kotlin.game.dto.GameStateDTO
import main.kotlin.game.dto.PlayerDTO
import main.kotlin.game.dto.SendGameStateToClientsDTO
import main.kotlin.lobby.Lobby
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.gamestate.GameStateNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class GameState : INetworkNewsPaperSubscriber {

    private val players = mutableListOf<Player>()

    private val gameStateLock = Object()

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectFromServerMessage(dto)
        }
    }

    private fun handleConnectToServerMessage(connectionDTO: ConnectionDTO) {
        synchronized(gameStateLock){
            Player(connectionDTO.id, 10 + players.size * 75, 10).also {player ->
                players.add(player)
                buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
            }
        }
    }

    private fun handleDisconnectFromServerMessage(dto: DisconnectDTO) {
        synchronized(gameStateLock){
            players.removeAll { it.sessionId == dto.id }
            buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
        }
    }

    private fun buildSendGameStateDTO(): SendGameStateToClientsDTO {
        return SendGameStateToClientsDTO(GameStateDTO().also { gameStateDTO ->
            players.forEach{player ->
                PlayerDTO(player.sessionId, player.xPosition, player.yPosition).also {playerDTO ->
                    gameStateDTO.players.add(playerDTO)
                }
            }
        })
    }

    fun initializeGameState(lobbyPlayers: MutableList<main.kotlin.lobby.Player>){
        synchronized(gameStateLock){
            lobbyPlayers.forEach {lobbyPlayer ->
                main.kotlin.game.Player(lobbyPlayer.id, 10 + players.size * 75, 10).also {player ->
                    players.add(player)
                }
            }
            buildSendGameStateDTO().also { GameStateNewsPaper.broadcast(it) }
        }
    }

}

