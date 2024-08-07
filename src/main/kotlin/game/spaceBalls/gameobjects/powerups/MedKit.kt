package main.kotlin.game.spaceBalls.gameobjects.powerups

import main.kotlin.game.engine.GameEventType
import main.kotlin.game.engine.Rectangle
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.Player

class MedKit(val game: SpaceBalls, override var xPos: Double, override var yPos: Double): PowerUp() {

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        if(player.isAlive) player.health++
        game.fireEvent(GameEventType.MEDKIT_PICKUP)
    }
}