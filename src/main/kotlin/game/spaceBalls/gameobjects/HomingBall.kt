package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.GameLoop
import main.kotlin.game.engine.Rectangle
import main.kotlin.game.engine.Vec2D
import main.kotlin.game.spaceBalls.SpaceBalls
import kotlin.math.sqrt

class HomingBall(
    val owner: Player,
    override var xPos: Double,
    override var yPos: Double,
    val game: SpaceBalls): GameObject()
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

    override fun spawnZone(): Rectangle {
        return Rectangle(xPos, yPos, RADIUS * 2.0, RADIUS * 2.0)
    }

    private fun move(direction: Vec2D){
        xPos += direction.x * (MOVEMENT_SPEED * game.speedFactor)
        yPos += direction.y * (MOVEMENT_SPEED * game.speedFactor)
    }

    private fun checkInvisibilityTimer(){
        if(System.currentTimeMillis() > spawnMillis + OWNER_INVISIBLE_TIME){
            ownerInvisible = false
        }
    }

    private fun determineHomeablePlayers(): Set<Player> {
        return if (ownerInvisible) {
            game.players.filter { it != owner && it.isAlive }.toSet()
        } else {
            game.players.filter { it.isAlive }.toSet()
        }
    }

    private fun getDirectionTowardsPlayer(player: Player): Vec2D {
        val xDiff: Double = player.xPos - xPos
        val yDiff: Double = player.yPos - yPos

        return Vec2D(xDiff, yDiff).also { vec ->
            sqrt(vec.x * vec.x + vec.y * vec.y).also { magnitude ->
                vec.x /= magnitude
                vec.y /= magnitude
            }
        }
    }

    private fun calculateDistance(player: Player): Double {
        return sqrt(
            ((player.xPos - xPos) * (player.xPos - xPos) +
                         (player.yPos - yPos) * (player.yPos - yPos))
        )
    }
}