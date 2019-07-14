package main.kotlin.lobby

import main.kotlin.game.GameMode
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.dto.BackToLobbyToClientDTO
import main.kotlin.game.spaceBalls.dto.BackToLobbyToServerDTO
import main.kotlin.game.zombies.Zombies
import main.kotlin.lobby.dto.*
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.newspaper.lobby.LobbyNewsPaper
import main.kotlin.newspaper.network.INetworkNewsPaperSubscriber
import main.kotlin.newspaper.network.NetworkNewsPaper
import main.kotlin.utilities.DTO

class Lobby: INetworkNewsPaperSubscriber {

    private val players = mutableListOf<Player>()
    private var selectedGameMode = GameMode.SPACE_BALLS.value

    private val lobbyStateLock = Object()

    init {
        NetworkNewsPaper.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectToServerMessage(dto)
            is StartGameToServerDTO -> handleStartGameToServerDTO()
            is ChooseNameToServerDTO -> handleChooseNameToServerMessage(dto)
            is BackToLobbyToServerDTO -> handleBackToLobbyToServerMessage(dto)
            is ChooseGameModeToServerDTO -> handleChooseGameModeToServerMessage(dto)
        }
    }

    private fun handleChooseGameModeToServerMessage(dto: ChooseGameModeToServerDTO) {
        synchronized(lobbyStateLock){
            selectedGameMode = dto.game
            buildSendLobbyStateDTO().also { LobbyNewsPaper.broadcast(it) }
        }
    }

    private fun handleBackToLobbyToServerMessage(dto: BackToLobbyToServerDTO) {
        synchronized(lobbyStateLock){
            players.find { pl -> pl.id == dto.playerId }.also {
                it?: return
                it.status = Player.Status.AVAILABLE.text
            }
            LobbyNewsPaper.broadcast(BackToLobbyToClientDTO().also { it.playerId = dto.playerId })
            buildSendLobbyStateDTO().also { LobbyNewsPaper.broadcast(it) }
        }
    }

    private fun handleChooseNameToServerMessage(dto: ChooseNameToServerDTO) {
        synchronized(lobbyStateLock){
            players.find { pl -> pl.id == dto.playerId }.also { player ->
                player?: return
                player.name = dto.chosenName
            }
            buildSendLobbyStateDTO().also { LobbyNewsPaper.broadcast(it) }
        }
    }

    private fun handleStartGameToServerDTO() {
       synchronized(lobbyStateLock){
           val availablePlayers = mutableSetOf<Player>()
           players.forEach { player ->
               if(player.status == Player.Status.AVAILABLE.text){
                   availablePlayers.add(player)
               }
           }
           when(selectedGameMode){
               GameMode.SPACE_BALLS.value -> {
                   val game = SpaceBalls()
                   game.initializeGameState(availablePlayers)
                   game.start()
                   availablePlayers.forEach {
                       it.status = Player.Status.IN_GAME.text
                   }
               }
               GameMode.ZOMBIES.value -> {
                   val game = Zombies()
                   game.initializeGameState(availablePlayers)
                   game.start()
                   availablePlayers.forEach {
                       it.status = Player.Status.IN_GAME.text
                   }
               }
               else -> print("Game is not yet implemented \n")
           }
       }
    }


    private fun handleDisconnectToServerMessage(dto: DisconnectDTO) {
        synchronized(lobbyStateLock){
            players.find{ pl -> pl.id == dto.id}.also {
                it?: return
                players.remove(it)
            }
            buildSendLobbyStateDTO().also { LobbyNewsPaper.broadcast(it) }
        }
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        synchronized(lobbyStateLock){
            Player(dto.id, "available", dto.id).also {
                players.add(it)
                LobbyNewsPaper.broadcast(buildSendLobbyStateDTO())
            }
        }
    }

    private fun buildSendLobbyStateDTO(): DTO {
        return SendLobbyStateToClientsDTO(LobbyStateDTO(selectedGameMode).also { lobbyStateDTO ->
            players.forEach { player ->
                PlayerDTO(player.id, player.status, player.name).also { playerDTO ->
                    lobbyStateDTO.players.add(playerDTO)
                }
            }
        })
    }

}