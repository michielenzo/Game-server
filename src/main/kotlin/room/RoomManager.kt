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
            is ConnectionDTO -> handleConnectToServerMsg(dto)
            is DisconnectDTO -> handleDisconnectToServerMsg(dto)
            is StartGameToServerDTO -> handleStartGameToServerMsg(dto)
            is ChooseNameToServerDTO -> handleChooseNameToServerMsg(dto)
            is BackToRoomToServerDTO -> handleBackToRoomToServerMsg(dto)
            is ChooseGameModeToServerDTO -> handleChooseGameModeToServerMsg(dto)
            is JoinRoomToServerDTO -> handleJoinRoomToServerMsg(dto)
            is KickPlayerToServerDTO -> handleKickPlayerToServerMsg(dto)
            is PromotePlayerToServerDTO -> handlePromotePlayerToServerMsg(dto)
        }
    }

    private fun handlePromotePlayerToServerMsg(dto: PromotePlayerToServerDTO) {
        if(dto.playerId == dto.playerToPromoteId) return

        synchronized(roomsStateLock){
            val roomLeader = findRoomByPlayerId(dto.playerId)
            val roomPlayerToKick = findRoomByPlayerId(dto.playerId)

            if(roomLeader != roomPlayerToKick) return

            roomLeader.promoteNewLeader(dto.playerId, dto.playerToPromoteId)
        }
    }

    private fun handleKickPlayerToServerMsg(dto: KickPlayerToServerDTO) {
        if(dto.playerId == dto.playerToKickId) return

        synchronized(roomsStateLock) {
            val roomLeader = findRoomByPlayerId(dto.playerId)
            val roomPlayerToKick = findRoomByPlayerId(dto.playerToKickId)

            if(roomLeader != roomPlayerToKick) return

            val kickedPlayer = roomLeader.kickPlayer(dto.playerToKickId)
            Room(kickedPlayer, generateUniqueRoomCode()).also {
                rooms.add(it)
            }
        }
    }

    private fun handleJoinRoomToServerMsg(dto: JoinRoomToServerDTO){
        val roomToLeave = findRoomByPlayerId(dto.playerId)
        val player = roomToLeave.players.first { it.id == dto.playerId }

        try {
            synchronized(roomsStateLock) {
                val roomToJoin = rooms.first { it.roomCode == dto.roomCode }
                roomToJoin.joinRoom(player)

                roomToLeave.removePlayer(dto.playerId)
                if(roomToLeave.players.size < 1) {
                    rooms.remove(roomToLeave)
                }
            }
        } catch (e: NoSuchElementException) {
            RoomNotFoundToClientDTO(dto.roomCode, player.id).also{ RoomPublisher.broadcast(it) }
        }
    }

    private fun handleConnectToServerMsg(dto: ConnectionDTO){
        Player(dto.id, "available", "Player 1").also{ player ->
            Room(player, generateUniqueRoomCode()).also { room ->
                rooms.add(room)
            }
        }
    }

    private fun handleDisconnectToServerMsg(dto: DisconnectDTO){
        findRoomByPlayerId(dto.id).also { room ->
            room.removePlayer(dto)
            if(room.players.size < 1) rooms.remove(room)
        }
    }

    private fun handleChooseNameToServerMsg(dto: ChooseNameToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.choosePlayerName(dto) }
    }

    private fun handleStartGameToServerMsg(dto: StartGameToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.startGame() }
    }

    private fun handleBackToRoomToServerMsg(dto: BackToRoomToServerDTO){
        findRoomByPlayerId(dto.playerId).also { it.backToRoom(dto) }
    }

    private fun handleChooseGameModeToServerMsg(dto: ChooseGameModeToServerDTO){
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