package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.HomingBall
import main.kotlin.game.spaceBalls.gameobjects.Player

class ControlInverter(val game: SpaceBalls, override val xPosition: Double, override val yPosition: Double)
    : GameObject(), IPowerUp
{
    override fun tick() {}

    override fun onPickUp(player: Player) {
        HomingBall(player, xPosition, yPosition, game).also { game.homingBalls.add(it) }
        game.fireEvent(GameEventType.PICKUP_CONTROL_INVERTER)
    }
}