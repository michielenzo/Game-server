package main.kotlin.game.gameobject

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
        INVERTER("inverter")
    }
}

