package main.kotlin.lobby.dto

import main.kotlin.publisher.MessageType
import main.kotlin.utilities.DTO

data class SendLobbyStateToClientsDTO(val lobbyState: LobbyStateDTO,
                                      var yourId: String,
                                      val messageType: String = MessageType.SEND_LOBBY_STATE_TO_CLIENTS.value): DTO()

data class LobbyStateDTO(val gameMode: String,
                         val players: MutableList<PlayerDTO> = mutableListOf()): DTO()

data class PlayerDTO(val id: String, val status: String, val name: String = id): DTO()

data class StartGameToServerDTO(val messageType: String = MessageType.START_GAME_TO_SERVER.value): DTO()

data class ChooseNameToServerDTO(var playerId: String,
                                 val chosenName: String,
                                 val messageType: String = MessageType.CHOOSE_NAME_TO_SERVER.value): DTO()

data class ChooseGameModeToServerDTO(val game: String,
                                     val messageType: String = MessageType.CHOOSE_GAMEMODE_TO_SERVER.value): DTO()

