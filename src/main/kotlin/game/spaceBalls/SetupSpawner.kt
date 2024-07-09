package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.gameobjects.GameObject
import main.kotlin.game.spaceBalls.gameobjects.Meteorite
import main.kotlin.game.spaceBalls.gameobjects.Player
import kotlin.random.Random

class SetupSpawner(val game: SpaceBalls) {

    companion object {
        private const val SPAWN_AREA_PADDING = 100.0

        val SPAWN_AREA: Rectangle = Rectangle(
            SPAWN_AREA_PADDING, SPAWN_AREA_PADDING,
            (SpaceBalls.DIMENSION_WIDTH - SPAWN_AREA_PADDING * 2),
            (SpaceBalls.DIMENSION_HEIGHT - SPAWN_AREA_PADDING * 2)
        )
    }

    fun spawnObjects(roomPlayers: Set<main.kotlin.room.Player>, numberOfMeteorites: Int){
        game.players.addAll(roomPlayers.map { Player(it.id, it.name, 0.0, 0.0, game) }.toList())

        game.meteorites.addAll((0..numberOfMeteorites).map {
            Meteorite(0.0,0.0, Meteorite.MovementDirection.getRandom(), game)
        })

        (game.players + game.meteorites).forEach{ determineStartPosition(it) }
    }

    private fun determineStartPosition(obj: GameObject){
        val maxAttempts = 50
        var attempts = 0

        do {
            val spawnPosition = generateRandomSpawnPosition()
            obj.xPos = spawnPosition.x
            obj.yPos = spawnPosition.y
            attempts++
        } while (spawnZonesAreOverlapping(obj) && attempts <= maxAttempts)
    }

    private fun generateRandomSpawnPosition(): Vec2D {
        val xPos = Random.nextDouble(SPAWN_AREA.topLeftCorner().x, SPAWN_AREA.topRightCorner().x)
        val yPos = Random.nextDouble(SPAWN_AREA.topLeftCorner().y, SPAWN_AREA.bottomLeftCorner().y)
        return Vec2D(xPos, yPos)
    }

    private fun spawnZonesAreOverlapping(obj: GameObject): Boolean {
        val overlappingObj = (game.players + game.meteorites).firstOrNull { other ->
            if(obj == other) return false

            val hitMarker = Collision.rectWithRect(other.spawnZone(), obj.spawnZone())
            when(hitMarker){
                Collision.HitMarker.NONE -> false
                else -> true
            }
        }

        return overlappingObj != null
    }
}