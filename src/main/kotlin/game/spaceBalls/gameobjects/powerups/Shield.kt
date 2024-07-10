package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.Rectangle
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.Player

class Shield(val game: SpaceBalls, override var xPos: Double, override var yPos: Double): PowerUp() {

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