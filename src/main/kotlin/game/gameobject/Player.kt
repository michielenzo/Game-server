package main.kotlin.game.gameobject

import main.kotlin.game.GameState

class Player(val sessionId: String, val name: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int): GameObject{

    companion object {
        const val WIDTH = 50
        const val HEIGHT = 50
    }

    @Volatile var wKey = false
    @Volatile var aKey = false
    @Volatile var sKey = false
    @Volatile var dKey = false

    private val speed = 3
    var health = 3
    var isAlive = true

    override fun tick() {
        move()
        checkWallCollision()
        checkHealth()
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

