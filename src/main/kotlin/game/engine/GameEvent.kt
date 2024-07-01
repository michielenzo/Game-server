package main.kotlin.game.engine

enum class GameEventType {
    PLAYER_METEORITE_COLLISION,
    METEORITES_UNFREEZE,
    SHIELD_PICKUP,
    MEDKIT_PICKUP,
    PLAYER_DIED,
    PICKUP_CONTROL_INVERTER,
    START_CONTROLS_INVERTED,
    INVERTER_PICKUP,
}

data class GameEvent(val type: GameEventType) {
    val data: HashMap<String, String> = HashMap()
}