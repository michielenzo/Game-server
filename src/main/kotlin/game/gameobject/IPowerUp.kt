package main.kotlin.game.gameobject

interface IPowerUp{

    val xPosition: Int
    val yPosition: Int

    fun onPickUp(player: Player)

}

enum class PowerUpType(val text: String){
    MED_KIT("med_kit")
}