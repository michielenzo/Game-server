package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.Rectangle
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.Player

class Inverter(val game: SpaceBalls, override var xPos: Double, override var yPos: Double): PowerUp() {

    override fun tick() {

    }

    override fun spawnZone(): Rectangle {
        return Rectangle(xPos, yPos, PowerUp.WIDTH, PowerUp.HEIGHT)
    }

    override fun onPickUp(player: Player) {
        player.game.meteorites.forEach { it.invert() }
        game.fireEvent(GameEventType.INVERTER_PICKUP)
    }
}