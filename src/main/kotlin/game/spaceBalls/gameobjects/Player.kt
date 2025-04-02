package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.dto.PlayerDTO
import main.kotlin.game.spaceBalls.gameobjects.powerups.PowerUp
import main.kotlin.game.spaceBalls.gameobjects.powerups.Shield

class Player(
    val sessionId: String,
    val name: String,
    @Volatile override var xPos: Double,
    @Volatile override var yPos: Double,
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

    override fun spawnZone(): Rectangle {
        val padding = 25
        return Rectangle(
            xPos - padding, yPos - padding, (WIDTH + padding * 2.0), (HEIGHT + padding * 2.0)
        )
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
        val powerUpsCollidingWith = mutableListOf<PowerUp>()
        game.powerUps.forEach { powerUp ->
            Rectangle(
                xPos, yPos,
                    WIDTH.toDouble(), HEIGHT.toDouble()).also { rectA ->
                Rectangle(
                    powerUp.xPos, powerUp.yPos,
                    PowerUp.WIDTH, PowerUp.HEIGHT
                ).also { rectB ->
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
            Rectangle(xPos, yPos, WIDTH.toDouble(), HEIGHT.toDouble()).also { rect ->
                Circle(ball.xPos, ball.yPos, HomingBall.RADIUS.toDouble()).also{ circle ->
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
        if(xPos < 0) xPos = 0.0
        if(xPos > SpaceBalls.DIMENSION_WIDTH - WIDTH)
            xPos = (SpaceBalls.DIMENSION_WIDTH - WIDTH).toDouble()
        if(yPos < 0) yPos = 0.0
        if(yPos > SpaceBalls.DIMENSION_HEIGHT - HEIGHT)
            yPos = (SpaceBalls.DIMENSION_HEIGHT - HEIGHT).toDouble()
    }

    private fun move(){
        if(controlsInverted){
            if(wKey) {yPos += SPEED * game.speedFactor}
            if(aKey) {xPos += SPEED * game.speedFactor}
            if(sKey) {yPos -= SPEED * game.speedFactor}
            if(dKey) {xPos -= SPEED * game.speedFactor}
        } else {
            if(wKey) {yPos -= SPEED * game.speedFactor}
            if(aKey) {xPos -= SPEED * game.speedFactor}
            if(sKey) {yPos += SPEED * game.speedFactor}
            if(dKey) {xPos += SPEED * game.speedFactor}
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

fun Player.toDTO(): PlayerDTO = PlayerDTO(
    id,
    sessionId,
    name,
    xPos,
    yPos,
    health,
    hasShield,
    controlsInverted
)
