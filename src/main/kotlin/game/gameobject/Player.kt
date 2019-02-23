package main.kotlin.game.gameobject

import javafx.scene.shape.Rectangle
import main.kotlin.game.GameState
import main.kotlin.utilities.Collision

class Player(val sessionId: String, val name: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int, val gameState: GameState): GameObject{

    companion object {
        const val WIDTH = 50
        const val HEIGHT = 50
    }

    @Volatile var wKey = false
    @Volatile var aKey = false
    @Volatile var sKey = false
    @Volatile var dKey = false

    private val speed = 3
    var health = 8
    var isAlive = true

    var hasShield = false
    var shieldStartTime: Long = 0

    override fun tick() {
        move()
        checkWallCollision()
        checkPowerUpCollision()
        checkHealth()
        checkShield()
    }

    private fun checkShield() {
        if(hasShield){
            if(System.currentTimeMillis() - shieldStartTime >= Shield.AFFECTION_TIME){
                hasShield = false
            }
        }
    }

    private fun checkPowerUpCollision() {
        val powerUpsCollidingWith = mutableListOf<IPowerUp>()
        gameState.powerUps.forEach { powerUp ->
            Rectangle(xPosition.toDouble(), yPosition.toDouble(),
                    WIDTH.toDouble(), HEIGHT.toDouble()).also { rectA ->
                Rectangle(powerUp.xPosition.toDouble(), powerUp.yPosition.toDouble(),
                        IPowerUp.WIDTH.toDouble(), IPowerUp.HEIGHT.toDouble()).also { rectB ->
                    if(Collision.rectangleWithRectangleCollision(rectA, rectB) == Collision.HitMarker.SOMEWHERE){
                        powerUp.onPickUp(this)
                        powerUpsCollidingWith.add(powerUp)
                    }
                }
            }
        }
        powerUpsCollidingWith.forEach { gameState.powerUps.remove(it) }
    }

    private fun checkWallCollision() {
        if(xPosition < 0) xPosition = 0
        if(xPosition > GameState.DIMENSION_WIDTH - WIDTH)
            xPosition = GameState.DIMENSION_WIDTH - WIDTH
        if(yPosition < 0) yPosition = 0
        if(yPosition > GameState.DIMENSION_HEIGHT - HEIGHT)
            yPosition = GameState.DIMENSION_HEIGHT - HEIGHT
    }

    private fun move(){
        if(wKey) {yPosition -= speed}
        if(aKey) {xPosition -= speed}
        if(sKey) {yPosition += speed}
        if(dKey) {xPosition += speed}
    }

    private fun checkHealth() {
        if(health <= 0){
            health = 0
            isAlive = false
        }
    }

}

