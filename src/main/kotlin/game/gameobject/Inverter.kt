package main.kotlin.game.gameobject

class Inverter(override val xPosition: Int, override val yPosition: Int) : GameObject, IPowerUp {

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        player.gameState.fireBalls.forEach { it.invert() }
    }

}