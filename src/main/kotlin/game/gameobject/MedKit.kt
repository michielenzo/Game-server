package main.kotlin.game.gameobject

class MedKit(override val xPosition: Int, override val yPosition: Int): GameObject, IPowerUp {

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        if(player.isAlive) player.health++
    }

}