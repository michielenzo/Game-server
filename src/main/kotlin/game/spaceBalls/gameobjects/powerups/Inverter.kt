package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.Player

class Inverter(override val xPosition: Double, override val yPosition: Double) : GameObject(), IPowerUp {

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        player.spaceBalls.fireBalls.forEach { it.invert() }
    }
}