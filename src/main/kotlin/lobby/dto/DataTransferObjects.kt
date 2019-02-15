package main.kotlin.lobby.dto

import main.kotlin.newspaper.MessageType
import main.kotlin.utilities.DTO

data class SendLobbyStateToClientsDTO(val lobbyState: LobbyStateDTO,
                                      val messageType: String = MessageType.SEND_LOBBY_STATE_TO_CLIENTS.value): DTO()

data class LobbyStateDTO(val players: MutableList<PlayerDTO> = mutableListOf()): DTO()

data class PlayerDTO(val id: String, val name: String = id): DTO()
