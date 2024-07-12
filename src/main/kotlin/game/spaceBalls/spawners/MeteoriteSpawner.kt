package main.kotlin.game.spaceBalls.spawners

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.SpaceBalls
import main.kotlin.game.spaceBalls.gameobjects.Meteorite
import kotlin.random.Random

class MeteoriteSpawner(val game: SpaceBalls) {

    companion object {
        const val MAX_AMOUNT = 13
        const val START_AMOUNT = 6
        const val INTERVAL = 30000

        private const val SPAWN_AREA_PADDING = 100.0

        val SPAWN_AREA: Rectangle = Rectangle(
            SPAWN_AREA_PADDING, SPAWN_AREA_PADDING,
            (SpaceBalls.DIMENSION_WIDTH - SPAWN_AREA_PADDING * 2),
            (SpaceBalls.DIMENSION_HEIGHT - SPAWN_AREA_PADDING * 2)
        )
    }

    private var millisSinceLastSpawn: Long = System.currentTimeMillis()
    private var started: Boolean = false

    fun tick(){
        if(timeToSpawn() && game.meteorites.size < MAX_AMOUNT) spawnMeteorite()
    }

    fun start(){
        started = true
        millisSinceLastSpawn = System.currentTimeMillis()
    }

    private fun timeToSpawn(): Boolean{
        if(!started) return false
        return if(System.currentTimeMillis() >= millisSinceLastSpawn + INTERVAL){
            millisSinceLastSpawn = System.currentTimeMillis(); true
        } else false
    }

    private fun spawnMeteorite(){
        val meteorite = Meteorite(0.0,0.0, Meteorite.MovementDirection.getRandom(), game)
        determineStartPosition(meteorite)
        game.meteorites.add(meteorite)
        game.fireEvent(GameEventType.METEORITE_SPAWNED, hashMapOf(
            "id" to meteorite.id.toString(),
            "direction" to meteorite.direction.toString()
        ))
    }

    private fun determineStartPosition(meteorite: Meteorite){
        val maxAttempts = 20
        var attempts = 0

        do {
            val spawnPosition = generateRandomSpawnPosition()
            meteorite.xPos = spawnPosition.x
            meteorite.yPos = spawnPosition.y
            attempts++
        } while (spawnZonesAreOverlappingWithPlayers(meteorite) && attempts <= maxAttempts)
    }

    private fun generateRandomSpawnPosition(): Vec2D {
        val xPos = Random.nextDouble(SPAWN_AREA.topLeftCorner().x, SPAWN_AREA.topRightCorner().x)
        val yPos = Random.nextDouble(SPAWN_AREA.topLeftCorner().y, SPAWN_AREA.bottomLeftCorner().y)
        return Vec2D(xPos, yPos)
    }

    private fun spawnZonesAreOverlappingWithPlayers(powerUp: Meteorite): Boolean {
        val overlappingObj = game.players.firstOrNull { player ->
            val hitMarker = Collision.rectWithRect(player.spawnZone(), powerUp.spawnZone())
            when(hitMarker){
                Collision.HitMarker.NONE -> false
                else -> true
            }
        }

        return overlappingObj != null
    }
}