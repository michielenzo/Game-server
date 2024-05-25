package main.kotlin.room

import main.kotlin.game.GameMode
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.dto.BackToRoomToClientDTO
import main.kotlin.game.spaceBalls.dto.BackToRoomToServerDTO
import main.kotlin.game.zombies.Zombies
import main.kotlin.room.dto.*
import main.kotlin.network.dto.ConnectionDTO
import main.kotlin.network.dto.DisconnectDTO
import main.kotlin.publisher.room.RoomPublisher
import main.kotlin.publisher.network.INetworkSubscriber
import main.kotlin.publisher.network.NetworkPublisher
import main.kotlin.utilities.DTO

class Room: INetworkSubscriber {

    private val players = mutableListOf<Player>()
    private var selectedGameMode = GameMode.SPACE_BALLS.value

    private val roomStateLock = Object()

    init {
        NetworkPublisher.subscribe(this)
    }

    override fun notifyNetworkNews(dto: DTO) {
        when(dto){
            is ConnectionDTO -> handleConnectToServerMessage(dto)
            is DisconnectDTO -> handleDisconnectToServerMessage(dto)
            is StartGameToServerDTO -> handleStartGameToServerDTO()
            is ChooseNameToServerDTO -> handleChooseNameToServerMessage(dto)
            is BackToRoomToServerDTO -> handleBackToRoomToServerMessage(dto)
            is ChooseGameModeToServerDTO -> handleChooseGameModeToServerMessage(dto)
        }
    }

    private fun handleChooseGameModeToServerMessage(dto: ChooseGameModeToServerDTO) {
        synchronized(roomStateLock){
            selectedGameMode = dto.game
            buildSendRoomStateDTO().also { RoomPublisher.broadcast(it) }
        }
    }

    private fun handleBackToRoomToServerMessage(dto: BackToRoomToServerDTO) {
        synchronized(roomStateLock){
            players.find { pl -> pl.id == dto.playerId }.also {
                it?: return
                it.status = Player.Status.AVAILABLE.text
            }
            RoomPublisher.broadcast(BackToRoomToClientDTO().also { it.playerId = dto.playerId })
            buildSendRoomStateDTO().also { RoomPublisher.broadcast(it) }
        }
    }

    private fun handleChooseNameToServerMessage(dto: ChooseNameToServerDTO) {
        synchronized(roomStateLock){
            players.find { pl -> pl.id == dto.playerId }.also { player ->
                player?: return
                player.name = dto.chosenName
            }
            buildSendRoomStateDTO().also { RoomPublisher.broadcast(it) }
        }
    }

    private fun handleStartGameToServerDTO() {
       synchronized(roomStateLock){
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
                   game.startGame()
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
        synchronized(roomStateLock){
            players.find{ pl -> pl.id == dto.id }.also {
                it?: return
                players.remove(it)
            }
            buildSendRoomStateDTO().also { RoomPublisher.broadcast(it) }
        }
    }

    private fun handleConnectToServerMessage(dto: ConnectionDTO) {
        synchronized(roomStateLock){
            Player(dto.id, "available", determinePlayerName()).also {
                players.add(it)
                RoomPublisher.broadcast(buildSendRoomStateDTO())
            }
        }
    }

    private fun determinePlayerName(): String {
        val playerNamePrefix = "Player "
        val playerNumbers = mutableSetOf<Int>()

        // Collect all existing player numbers
        players.forEach { player ->
            if (player.name.startsWith(playerNamePrefix)) {
                val numberPart = player.name.substring(playerNamePrefix.length)
                numberPart.toIntOrNull()?.let {
                    playerNumbers.add(it)
                }
            }
        }

        // Determine the smallest missing number
        var newPlayerNumber = 1
        while (newPlayerNumber in playerNumbers) {
            newPlayerNumber++
        }

        // Check if the missing number is within the range of existing numbers
        if (newPlayerNumber > (playerNumbers.maxOrNull() ?: 0)) {
            newPlayerNumber = (playerNumbers.maxOrNull() ?: 0) + 1
        }

        return "Player $newPlayerNumber"
    }

    private fun buildSendRoomStateDTO(): DTO {
        return SendRoomStateToClientsDTO(RoomStateDTO(selectedGameMode).also { roomStateDTO ->
            players.forEach { player ->
                PlayerDTO(player.id, player.status, player.name).also { playerDTO ->
                    roomStateDTO.players.add(playerDTO)
                }
            }
        }, "")
    }
}