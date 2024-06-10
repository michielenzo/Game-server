package main.kotlin.room

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
}