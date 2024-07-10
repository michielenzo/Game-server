package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.Rectangle
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.Player

abstract class PowerUp: GameObject() {

    companion object {
        const val WIDTH = 40.0
        const val HEIGHT = 40.0
    }

    abstract fun onPickUp(player: Player)

    override fun spawnZone(): Rectangle {
        return Rectangle(xPos, yPos, WIDTH, HEIGHT)
    }

    enum class Type(val text: String){
        MED_KIT("med_kit"),
        SHIELD("shield"),
        INVERTER("inverter"),
        CONTROL_INVERTER("control_inverter")
    }
}

