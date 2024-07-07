package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.powerups.IPowerUp
import main.kotlin.game.spaceBalls.gameobjects.powerups.Shield

class Player(
    val sessionId: String,
    val name: String,
    @Volatile var xPosition: Double,
    @Volatile var yPosition: Double,
    val game: SpaceBalls
): GameObject() {

    companion object {
        const val WIDTH = 60
        const val HEIGHT = 42
        const val SPEED: Int = 165
    }

    @Volatile var wKey = false
    @Volatile var aKey = false
    @Volatile var sKey = false
    @Volatile var dKey = false

    var health: Int = 5
    var isAlive: Boolean = true

    var hasShield: Boolean = false
    var shieldStartTime: Long = 0

    var controlsInverted: Boolean = false
    private var controlsInvertedStartTime: Long = 0

    override fun tick() {
        if(isAlive) move()
        checkWallCollision()
        checkPowerUpCollision()
        if(isAlive) checkHomingBallCollision()
        checkHealth()
        checkShield()
        checkControlsInverted()
    }

    private fun checkShield() {
        if(hasShield){
            if(System.currentTimeMillis() - shieldStartTime >= Shield.AFFECTION_TIME){
                hasShield = false
            }
        }
    }

    private fun checkControlsInverted() {
        if(controlsInverted){
            if(System.currentTimeMillis() - controlsInvertedStartTime >= HomingBall.CONTROLS_INVERTED_AFFECTION_TIME){
                controlsInverted = false
            }
        }
    }

    private fun checkPowerUpCollision() {
        val powerUpsCollidingWith = mutableListOf<IPowerUp>()
        game.powerUps.forEach { powerUp ->
            Rectangle(
                xPosition, yPosition,
                    WIDTH.toDouble(), HEIGHT.toDouble()).also { rectA ->
                Rectangle(
                    powerUp.xPosition, powerUp.yPosition,
                        IPowerUp.WIDTH.toDouble(), IPowerUp.HEIGHT.toDouble()).also { rectB ->
                    if(Collision.rectWithRect(rectA, rectB) == Collision.HitMarker.SOMEWHERE){
                        powerUp.onPickUp(this)
                        powerUpsCollidingWith.add(powerUp)
                    }
                }
            }
        }
        powerUpsCollidingWith.forEach { game.powerUps.remove(it) }
    }

    private fun checkHomingBallCollision() {
        val homingBallsCollidingWith = mutableListOf<HomingBall>()
        game.homingBalls.forEach { ball ->
            Rectangle(xPosition, yPosition, WIDTH.toDouble(), HEIGHT.toDouble()).also { rect ->
                Circle(ball.xPosition, ball.yPosition, HomingBall.RADIUS.toDouble()).also{ circle ->
                    if(Collision.rectWithCircle(rect, circle) != Collision.HitMarker.NONE){
                        homingBallsCollidingWith.add(ball)
                    }
                }
            }
        }

        homingBallsCollidingWith.forEach {
            if((it.owner != this || !it.ownerInvisible) && isAlive){
                applyControlInverterEffect()
                game.homingBalls.remove(it)
            }
        }
    }

    private fun applyControlInverterEffect(){
        controlsInverted = true
        controlsInvertedStartTime = System.currentTimeMillis()
        game.fireEvent(GameEventType.START_CONTROLS_INVERTED)
    }

    private fun checkWallCollision() {
        if(xPosition < 0) xPosition = 0.0
        if(xPosition > SpaceBalls.DIMENSION_WIDTH - WIDTH)
            xPosition = (SpaceBalls.DIMENSION_WIDTH - WIDTH).toDouble()
        if(yPosition < 0) yPosition = 0.0
        if(yPosition > SpaceBalls.DIMENSION_HEIGHT - HEIGHT)
            yPosition = (SpaceBalls.DIMENSION_HEIGHT - HEIGHT).toDouble()
    }

    private fun move(){
        if(controlsInverted){
            if(wKey) {yPosition += SPEED * GameLoop.SPEED_FACTOR}
            if(aKey) {xPosition += SPEED * GameLoop.SPEED_FACTOR}
            if(sKey) {yPosition -= SPEED * GameLoop.SPEED_FACTOR}
            if(dKey) {xPosition -= SPEED * GameLoop.SPEED_FACTOR}
        } else {
            if(wKey) {yPosition -= SPEED * GameLoop.SPEED_FACTOR}
            if(aKey) {xPosition -= SPEED * GameLoop.SPEED_FACTOR}
            if(sKey) {yPosition += SPEED * GameLoop.SPEED_FACTOR}
            if(dKey) {xPosition += SPEED * GameLoop.SPEED_FACTOR}
        }
    }

    private fun checkHealth() {
        if(health <= 0 && isAlive){
            health = 0
            isAlive = false
            game.gameEvents.add(GameEvent(GameEventType.PLAYER_DIED))
        }
    }
}

fun Player.spawnZone(): Rectangle {
    val padding = 25
    return Rectangle(
        xPosition - padding, yPosition - padding,
        (Player.WIDTH + padding * 2).toDouble(),
        (Player.HEIGHT + padding * 2).toDouble()
    )
}

