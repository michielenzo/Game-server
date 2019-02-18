package main.kotlin.game.gameobject

import main.kotlin.game.GameState

class FireBall(var xPosition: Int, var yPosition: Int, private var direction: MovementDirection): GameObject {

    val diameter = 50
    private val speed = 4
    //private var collisionBreak = false


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
        checkCollisionWithTheWall().also { wall ->
            wall?: return
            handleWallCollision(wall)
        }
    }

    private fun checkCollisionWithTheWall(): WallCollision?{
        return when {
            //collisionBreak -> return null
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
        //collisionBreak = true
    }

    enum class WallCollision{
        ROOF, FLOOR, LEFT_WALL, RIGHT_WALL
    }

    enum class MovementDirection{
        UP_LEFT, UP_RIGHT,
        DOWN_LEFT, DOWN_RIGHT
    }

}