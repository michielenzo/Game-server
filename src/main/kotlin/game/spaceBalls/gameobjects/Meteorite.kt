package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.SpaceBalls
import kotlin.random.Random

class Meteorite(
    override var xPos: Double,
    override var yPos: Double,
    var direction: MovementDirection,
    private val game: SpaceBalls
): GameObject() {

    companion object{
        const val DIAMETER = 50.0
    }

    enum class State {
        FROZEN,
        LOOSE
    }

    private val speed = 220
    private val playerCollision = mutableListOf<PlayerCollision>()
    var state: State = State.FROZEN

    init {
        game.players.forEach {
            playerCollision.add(PlayerCollision(it, Collision.HitMarker.NONE))
        }

        Scheduler.schedule(SpaceBalls.COUNTDOWN_MILLIS) { state = State.LOOSE }
    }

    override fun tick() {
        if(state == State.LOOSE){
            move()
            checkCollision()
        }
    }

    override fun spawnZone(): Rectangle {
        val padding = 40
        return Rectangle(
            xPos - padding, yPos - padding,
            DIAMETER + padding * 2.0, DIAMETER + padding * 2.0
        )
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
            MovementDirection.UP_LEFT -> { xPos -= resolvedSpeed; yPos -= resolvedSpeed }
            MovementDirection.UP_RIGHT -> { xPos += resolvedSpeed; yPos -= resolvedSpeed }
            MovementDirection.DOWN_LEFT -> { xPos -= resolvedSpeed; yPos += resolvedSpeed }
            MovementDirection.DOWN_RIGHT -> { xPos += resolvedSpeed; yPos += resolvedSpeed }
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
            game.gameEvents.add(GameEvent(GameEventType.PLAYER_METEORITE_COLLISION))
        } else coll.timeOutTicks--
    }

    private fun checkCollisionWithPlayers() {
        game.players.forEach { player ->
            Rectangle(player.xPos, player.yPos, Player.WIDTH.toDouble(), Player.HEIGHT.toDouble()).also { rect ->
                Circle(xPos, yPos, (DIAMETER/2)).also { circle ->
                    Collision.rectWithCircle(rect, circle).also { hitMarker ->
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
            xPos <= 0 + DIAMETER/2 -> WallCollision.LEFT_WALL
            xPos >= SpaceBalls.DIMENSION_WIDTH -> WallCollision.RIGHT_WALL
            yPos <= 0 + DIAMETER/2 -> WallCollision.ROOF
            yPos >= SpaceBalls.DIMENSION_HEIGHT - DIAMETER/2 -> WallCollision.FLOOR
            else -> return null
        }
    }

    private fun handleWallCollision(wall: WallCollision){
        when(wall){
            WallCollision.ROOF -> {
                yPos = (0 + DIAMETER/2)
                direction = if(direction == MovementDirection.UP_LEFT) MovementDirection.DOWN_LEFT
                            else                                       MovementDirection.DOWN_RIGHT
            }
            WallCollision.FLOOR -> {
                yPos = (SpaceBalls.DIMENSION_HEIGHT - DIAMETER/2)
                direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.UP_LEFT
                            else                                         MovementDirection.UP_RIGHT
            }
            WallCollision.LEFT_WALL -> {
                xPos = (0 + DIAMETER/2)
                direction = if(direction == MovementDirection.DOWN_LEFT) MovementDirection.DOWN_RIGHT
                            else                                         MovementDirection.UP_RIGHT
            }
            WallCollision.RIGHT_WALL -> {
                xPos = (SpaceBalls.DIMENSION_WIDTH - DIAMETER/2)
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
        DOWN_LEFT, DOWN_RIGHT;

        companion object{ fun getRandom(): MovementDirection = entries[Random.nextInt(entries.size)] }
    }

    data class PlayerCollision(val player: Player, var hitMarker: Collision.HitMarker, var timeOutTicks: Int = 0){
        companion object {
            const val MAX_TIMEOUT_TICKS = 20
        }
    }
}