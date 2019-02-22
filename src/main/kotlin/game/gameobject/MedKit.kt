package main.kotlin.game.gameobject

class MedKit(val xPosition: Int, val yPosition: Int): GameObject, IPowerUp {

    companion object {
        const val WIDTH = 40
        const val HEIGHT = 40
    }

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        player.health++
    }

}