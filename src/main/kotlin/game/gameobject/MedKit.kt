package main.kotlin.game.gameobject

class MedKit(override val xPosition: Int, override val yPosition: Int): GameObject, IPowerUp {

    companion object {
        const val WIDTH = 40
        const val HEIGHT = 40
    }

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        if(player.isAlive) player.health++
    }

}