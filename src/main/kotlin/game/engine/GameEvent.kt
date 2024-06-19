package main.kotlin.game.engine

enum class GameEventType {
    PLAYER_METEORITE_COLLISION,
    METEORITES_UNFREEZE
}

data class GameEvent(val type: GameEventType) {
    val data: HashMap<String, String> = HashMap()
}