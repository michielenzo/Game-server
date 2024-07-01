package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.Player

class Inverter(val game: SpaceBalls, override val xPosition: Double, override val yPosition: Double) : GameObject(), IPowerUp {

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        player.game.meteorites.forEach { it.invert() }
        game.fireEvent(GameEventType.INVERTER_PICKUP)
    }
}