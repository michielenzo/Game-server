package main.kotlin.game.gameobject

import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import main.kotlin.game.GameState
import main.kotlin.utilities.Collision

class FireBall(var xPosition: Int, var yPosition: Int,
               private var direction: MovementDirection,
               private val game: GameState)
: GameObject {

    val diameter = 50
    private val speed = 4
    val playerCollision = mutableListOf<PlayerCollision>()

    init {
        game.players.forEach {
            playerCollision.add(PlayerCollision(it, Collision.HitMarker.NONE))
        }
    }

    override fun tick() {
        move()
        checkCollision()
    }

    private fun move(){
        when(direction){
            MovementDirection.UP_LEFT -> { xPosition -= speed; yPosition -= speed }
            MovementDirection.UP_RIGHT -> { xPosition += speed; yPosition -= speed }
            MovementDirection.DOWN_LEFT -> { xPosition -= speed; yPosition += speed }
            MovementDirection.DOWN_RIGHT -> { xPosition += speed; yPosition += speed }
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
        playerCollision.forEach {
            when(it.hitMarker){
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
            }
            if(it.hitMarker != Collision.HitMarker.NONE){
                game.players.find { pl -> pl.sessionId == it.player.sessionId }.also { player ->
                    player?: return
                    player.health--
                }
            }
        }
    }

    private fun checkCollisionWithPlayers() {
        game.players.forEach { player ->
            Rectangle(player.xPosition.toDouble(), player.yPosition.toDouble(), Player.WIDTH.toDouble(), Player.HEIGHT.toDouble()).also { rect ->
                Circle().apply { this.radius = (diameter/2).toDouble(); centerX = xPosition.toDouble(); centerY = yPosition.toDouble() }.also { circle ->
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
            xPosition >= GameState.DIMENSION_WIDTH -> WallCollision.RIGHT_WALL
            yPosition <= 0 + diameter/2 -> WallCollision.ROOF
            yPosition >= GameState.DIMENSION_HEIGHT - diameter/2 -> WallCollision.FLOOR
            else -> return null
        }
    }

    private fun handleWallCollision(wall: WallCollision){
        when(wall){
            WallCollision.ROOF -> {
                yPosition = 0 + diameter/2
                direction = if(direction == MovementDirection.UP_LEFT) MovementDirection.DOWN_LEFT
                            else                                       MovementDirection.DOWN_RIGHT
            }
            WallCollision.FLOOR -> {
                yPosition = GameState.DIMENSION_HEIGHT - diameter/2
                direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.UP_LEFT
                            else                                         MovementDirection.UP_RIGHT
            }
            WallCollision.LEFT_WALL -> {
                xPosition = 0 + diameter/2
                direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.DOWN_RIGHT
                            else                                         MovementDirection.UP_RIGHT
            }
            WallCollision.RIGHT_WALL -> {
                xPosition = GameState.DIMENSION_WIDTH - diameter/2
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

    data class PlayerCollision(val player: Player, var hitMarker: Collision.HitMarker)

}