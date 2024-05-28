package main.kotlin.room

import main.kotlin.game.spaceBalls.dto.BackToRoomToServerDTO
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.publisher.network.INetworkSubscriber
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.publisher.room.RoomPublisher
import main.kotlin.room.dto.*
import main.kotlin.utilities.DTO

class RoomManager: INetworkSubscriber {

    private val rooms: ArrayList<Room> = ArrayList()

    private val roomsStateLock = Object()

    init {
        NetworkPublisher.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectToServerMessage(dto)
            is StartGameToServerDTO -> handleStartGameToServerDTO(dto)
            is ChooseNameToServerDTO -> handleChooseNameToServerMessage(dto)
            is BackToRoomToServerDTO -> handleBackToRoomToServerMessage(dto)
            is ChooseGameModeToServerDTO -> handleChooseGameModeToServerMessage(dto)
            is JoinRoomToServerDTO -> handleJoinRoomToServerMessage(dto)
        }
    }

    private fun handleJoinRoomToServerMessage(dto: JoinRoomToServerDTO){
        val roomToLeave = findRoomByPlayerId(dto.playerId)
        val player = roomToLeave.players.first { it.id == dto.playerId }

        try {
            synchronized(roomsStateLock) {
                // Join new Room
                val roomToJoin = rooms.first { it.roomCode == dto.roomCode }
                roomToJoin.players.add(player)
                roomToJoin.buildSendRoomStateDTO().also { RoomPublisher.broadcast(it) }

                // Leave old Room
                roomToLeave.removePlayer(dto.playerId)
                if(roomToLeave.players.size < 1) {
                    rooms.remove(roomToLeave)
                } else {
                    roomToLeave.buildSendRoomStateDTO().also { RoomPublisher.broadcast(it) }
                }
            }
        } catch (e: NoSuchElementException) {
            RoomNotFoundToClientDTO(dto.roomCode, player.id).also{ RoomPublisher.broadcast(it) }
        }
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO){
        Room(dto.id, generateUniqueRoomCode()).also {
            rooms.add(it)
        }
    }

    private fun handleDisconnectToServerMessage(dto: DisconnectDTO){
        findRoomByPlayerId(dto.id).also { room ->
            room.removePlayer(dto)
            if(room.players.size < 1) rooms.remove(room)
        }
    }

    private fun handleChooseNameToServerMessage(dto: ChooseNameToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.choosePlayerName(dto) }
    }

    private fun handleStartGameToServerDTO(dto: StartGameToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.startGame() }
    }

    private fun handleBackToRoomToServerMessage(dto: BackToRoomToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.backToRoom(dto) }
    }

    private fun handleChooseGameModeToServerMessage(dto: ChooseGameModeToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.chooseGameMode(dto) }
    }

    private fun findRoomByPlayerId(playerId: String): Room {
        return rooms.first { room -> room.playerInRoom(playerId) }
    }

    private fun findPlayerById(playerId: String): Player {
        rooms.first { room -> room.playerInRoom(playerId) }.also { room ->
            return room.players.first{ it.id == playerId }
        }
    }

    private fun generateUniqueRoomCode(length: Int = 5): String {
        var roomCode: String
        val upperCaseLetters = ('A'..'Z').toList()

        do {
            roomCode = (1..length)
                .map { upperCaseLetters.random() }
                .joinToString("")
        } while (
            rooms.any { it.roomCode == roomCode }
        )

        return roomCode
    }
}