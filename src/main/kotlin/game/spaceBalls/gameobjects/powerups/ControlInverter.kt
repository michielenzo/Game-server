package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.HomingBall
import main.kotlin.game.spaceBalls.gameobjects.Player

class ControlInverter(override val xPosition: Int, override val yPosition: Int, val spaceBalls: SpaceBalls)
    : GameObject, IPowerUp
{
    override fun tick() {}

    override fun onPickUp(player: Player) {
        HomingBall(player, xPosition, yPosition, spaceBalls).also { spaceBalls.homingBalls.add(it) }
    }
}