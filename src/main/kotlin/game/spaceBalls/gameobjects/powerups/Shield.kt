package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.Player

class Shield(val game: SpaceBalls, override val xPosition: Double, override val yPosition: Double) : GameObject(), IPowerUp {

    companion object {
        const val AFFECTION_TIME = 10000
    }

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        if(player.isAlive) {
            player.hasShield = true
            player.shieldStartTime = System.currentTimeMillis()
            game.fireEvent(GameEventType.SHIELD_PICKUP)
        }
    }
}