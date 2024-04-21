package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.GameLoop
import main.kotlin.game.engine.Vec2D
import main.kotlin.game.spaceBalls.SpaceBalls
import kotlin.math.sqrt

class HomingBall(
    val owner: Player,
    var xPosition: Double,
    var yPosition: Double,
    val spaceBalls: SpaceBalls): GameObject()
{
    companion object{
        const val OWNER_INVISIBLE_TIME: Long = 5000
        const val MOVEMENT_SPEED: Long = 138
        const val RADIUS: Int = 25
        const val CONTROLS_INVERTED_AFFECTION_TIME: Long = 6000
    }

    var ownerInvisible: Boolean = true
    private val spawnMillis: Long = System.currentTimeMillis()


    override fun tick() {
       if(ownerInvisible) checkInvisibilityTimer()
       val homeablePlayers: Set<Player> = determineHomeablePlayers()
       val closestPlayer: Player? = homeablePlayers.minByOrNull { calculateDistance(it) }
       if (closestPlayer !== null) move(getDirectionTowardsPlayer(closestPlayer))
    }

    private fun move(direction: Vec2D){
        xPosition += direction.x * (MOVEMENT_SPEED * GameLoop.SPEED_FACTOR)
        yPosition += direction.y * (MOVEMENT_SPEED * GameLoop.SPEED_FACTOR)
    }

    private fun checkInvisibilityTimer(){
        if(System.currentTimeMillis() > spawnMillis + OWNER_INVISIBLE_TIME){
            ownerInvisible = false
        }
    }

    private fun determineHomeablePlayers(): Set<Player> {
        return if (ownerInvisible) {
            spaceBalls.players.filter { it != owner && it.isAlive }.toSet()
        } else {
            spaceBalls.players.filter { it.isAlive }.toSet()
        }
    }

    private fun getDirectionTowardsPlayer(player: Player): Vec2D {
        val xDiff: Double = player.xPosition - xPosition
        val yDiff: Double = player.yPosition - yPosition

        return Vec2D(xDiff, yDiff).also { vec ->
            sqrt(vec.x * vec.x + vec.y * vec.y).also { magnitude ->
                vec.x /= magnitude
                vec.y /= magnitude
            }
        }
    }

    private fun calculateDistance(player: Player): Double {
        return sqrt(
            ((player.xPosition - xPosition) * (player.xPosition - xPosition) +
                         (player.yPosition - yPosition) * (player.yPosition - yPosition))
        )
    }
}