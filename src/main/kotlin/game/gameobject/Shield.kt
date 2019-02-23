package main.kotlin.game.gameobject

class Shield(override val xPosition: Int, override val yPosition: Int) : GameObject, IPowerUp {

    companion object {
        const val AFFECTION_TIME = 10000
    }

    override fun tick() {

    }

    override fun onPickUp(player: Player) {
        if(player.isAlive) {
            player.hasShield = true
            player.shieldStartTime = System.currentTimeMillis()
        }
    }

}