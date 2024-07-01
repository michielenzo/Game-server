package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.Player

class MedKit(val game: SpaceBalls, override val xPosition: Double, override val yPosition: Double): GameObject(), IPowerUp {

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        if(player.isAlive) player.health++
        game.fireEvent(GameEventType.MEDKIT_PICKUP)
    }
}