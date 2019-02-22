package main.kotlin.game.gameobject

interface IPowerUp{
    fun onPickUp(player: Player)
}

enum class PowerUpType(val text: String){
    MED_KIT("med_kit")
}