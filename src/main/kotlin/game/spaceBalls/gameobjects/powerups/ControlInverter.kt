package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.Rectangle
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.HomingBall
import main.kotlin.game.spaceBalls.gameobjects.Player

class ControlInverter(val game: SpaceBalls, override var xPos: Double, override var yPos: Double): PowerUp()
{
    override fun tick() {}
    override fun spawnZone(): Rectangle {
        return Rectangle(xPos, yPos, PowerUp.WIDTH, PowerUp.HEIGHT)
    }

    override fun onPickUp(player: Player) {
        HomingBall(player, xPos, yPos, game).also { game.homingBalls.add(it) }
        game.fireEvent(GameEventType.PICKUP_CONTROL_INVERTER)
    }
}