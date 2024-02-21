package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.spaceBalls.gameobjects.Player

interface IPowerUp{

    val xPosition: Int
    val yPosition: Int

    companion object {
        const val WIDTH = 40
        const val HEIGHT = 40
    }

    fun onPickUp(player: Player)

    enum class PowerUpType(val text: String){
        MED_KIT("med_kit"),
        SHIELD("shield"),
        INVERTER("inverter"),
        CONTROL_INVERTER("control_inverter")
    }
}

