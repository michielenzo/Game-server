package main.kotlin.room

import main.kotlin.room.dto.PlayerDTO

data class Player (
    val id: String,
    var status: Status,
    var name: String = id,
) {
    enum class Status(val text: String){
        AVAILABLE("available"),
        IN_GAME("in game"),
        READY("ready")
    }

    fun toPlayerDTO(): PlayerDTO{
        return PlayerDTO(id, status.text, name)
    }
}