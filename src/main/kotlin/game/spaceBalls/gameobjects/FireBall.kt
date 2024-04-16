package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.Circle
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.engine.Collision
import main.kotlin.game.engine.GameLoop
import main.kotlin.game.engine.Rectangle

class FireBall(var xPosition: Double, var yPosition: Double,
               private var direction: MovementDirection,
               private val game: SpaceBalls)
: GameObject {

    private val diameter = 50
    private val speed = 220
    private val playerCollision = mutableListOf<PlayerCollision>()

    init {
        game.players.forEach {
            playerCollision.add(PlayerCollision(it, Collision.HitMarker.NONE))
        }
    }

    override fun tick() {
        move()
        checkCollision()
    }

    fun invert(){
        direction = when(direction){
            MovementDirection.UP_LEFT -> MovementDirection.DOWN_RIGHT
            MovementDirection.UP_RIGHT -> MovementDirection.DOWN_LEFT
            MovementDirection.DOWN_LEFT -> MovementDirection.UP_RIGHT
            MovementDirection.DOWN_RIGHT -> MovementDirection.UP_LEFT
        }
    }

    private fun move(){
        val resolvedSpeed = speed * GameLoop.SPEED_FACTOR
        when(direction){
            MovementDirection.UP_LEFT -> { xPosition -= resolvedSpeed; yPosition -= resolvedSpeed }
            MovementDirection.UP_RIGHT -> { xPosition += resolvedSpeed; yPosition -= resolvedSpeed }
            MovementDirection.DOWN_LEFT -> { xPosition -= resolvedSpeed; yPosition += resolvedSpeed }
            MovementDirection.DOWN_RIGHT -> { xPosition += resolvedSpeed; yPosition += resolvedSpeed }
        }
    }

    private fun checkCollision(){
        checkCollisionWithPlayers()
        handlePlayerCollision()
        checkCollisionWithTheWall().also { wall ->
            wall?: return
            handleWallCollision(wall)
        }
    }

    private fun handlePlayerCollision() {
        playerCollision.forEach {coll ->
            if(coll.player.isAlive){
                when(coll.hitMarker){
                    Collision.HitMarker.ROOF -> {
                        direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.UP_LEFT
                        else MovementDirection.UP_RIGHT
                    }
                    Collision.HitMarker.FLOOR -> {
                        direction = if(direction == MovementDirection.UP_RIGHT) MovementDirection.DOWN_RIGHT
                        else MovementDirection.DOWN_LEFT
                    }
                    Collision.HitMarker.LEFT_WALL -> {
                        direction = if(direction == MovementDirection.UP_RIGHT) MovementDirection.UP_LEFT
                        else MovementDirection.DOWN_LEFT
                    }
                    Collision.HitMarker.RIGHT_WALL -> {
                        direction = if(direction == MovementDirection.UP_LEFT) MovementDirection.UP_RIGHT
                        else MovementDirection.DOWN_RIGHT
                    }
                    Collision.HitMarker.BOTTOM_RIGHT_CORNER -> { direction = MovementDirection.DOWN_RIGHT }
                    Collision.HitMarker.BOTTOM_LEFT_CORNER -> { direction = MovementDirection.DOWN_LEFT }
                    Collision.HitMarker.TOP_RIGHT_CORNER -> { direction = MovementDirection.UP_RIGHT }
                    Collision.HitMarker.TOP_LEFT_CORNER -> { direction = MovementDirection.UP_LEFT }
                    Collision.HitMarker.INSIDE -> {}
                    Collision.HitMarker.NONE -> {}
                    Collision.HitMarker.SOMEWHERE -> {}
                }
                damagePlayer(coll)
            }
        }
    }

    private fun damagePlayer(coll: PlayerCollision) {
        if (coll.hitMarker != Collision.HitMarker.NONE && coll.timeOutTicks <= 0) {
            game.players.find { pl -> pl.sessionId == coll.player.sessionId }.also { player ->
                player ?: return
                if (!player.hasShield) player.health--
                coll.timeOutTicks = PlayerCollision.MAX_TIMEOUT_TICKS
            }
        } else coll.timeOutTicks--
    }

    private fun checkCollisionWithPlayers() {
        game.players.forEach { player ->
            Rectangle(player.xPosition.toDouble(), player.yPosition.toDouble(), Player.WIDTH.toDouble(), Player.HEIGHT.toDouble()).also { rect ->
                Circle(xPosition, yPosition, (diameter/2).toDouble()).also { circle ->
                    Collision.rectangleWithCircleCollision(rect, circle).also { hitMarker ->
                        playerCollision.find { pl -> pl.player.sessionId == player.sessionId }.also { collision ->
                            collision?: return
                            collision.hitMarker = hitMarker
                        }
                    }
                }
            }
        }
    }

    private fun checkCollisionWithTheWall(): WallCollision?{
        return when {
            xPosition <= 0 + diameter/2 -> WallCollision.LEFT_WALL
            xPosition >= SpaceBalls.DIMENSION_WIDTH -> WallCollision.RIGHT_WALL
            yPosition <= 0 + diameter/2 -> WallCollision.ROOF
            yPosition >= SpaceBalls.DIMENSION_HEIGHT - diameter/2 -> WallCollision.FLOOR
            else -> return null
        }
    }

    private fun handleWallCollision(wall: WallCollision){
        when(wall){
            WallCollision.ROOF -> {
                yPosition = (0 + diameter/2).toDouble()
                direction = if(direction == MovementDirection.UP_LEFT) MovementDirection.DOWN_LEFT
                            else                                       MovementDirection.DOWN_RIGHT
            }
            WallCollision.FLOOR -> {
                yPosition = (SpaceBalls.DIMENSION_HEIGHT - diameter/2).toDouble()
                direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.UP_LEFT
                            else                                         MovementDirection.UP_RIGHT
            }
            WallCollision.LEFT_WALL -> {
                xPosition = (0 + diameter/2).toDouble()
                direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.DOWN_RIGHT
                            else                                         MovementDirection.UP_RIGHT
            }
            WallCollision.RIGHT_WALL -> {
                xPosition = (SpaceBalls.DIMENSION_WIDTH - diameter/2).toDouble()
                direction = if(direction == MovementDirection.DOWN_RIGHT) MovementDirection.DOWN_LEFT
                else                                                      MovementDirection.UP_LEFT
            }
        }
    }

    enum class WallCollision{
        ROOF, FLOOR, LEFT_WALL, RIGHT_WALL
    }

    enum class MovementDirection{
        UP_LEFT, UP_RIGHT,
        DOWN_LEFT, DOWN_RIGHT
    }

    data class PlayerCollision(val player: Player, var hitMarker: Collision.HitMarker, var timeOutTicks: Int = 0){
        companion object {
            const val MAX_TIMEOUT_TICKS = 20
        }
    }
}