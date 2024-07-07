package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.gameobjects.Player
import main.kotlin.game.spaceBalls.gameobjects.spawnZone
import kotlin.random.Random

class PlayerSpawner(val game: SpaceBalls) {

    companion object {
        private const val SPAWN_AREA_PADDING = 100.0

        val SPAWN_AREA: Rectangle = Rectangle(
            SPAWN_AREA_PADDING, SPAWN_AREA_PADDING,
            (SpaceBalls.DIMENSION_WIDTH - SPAWN_AREA_PADDING * 2),
            (SpaceBalls.DIMENSION_HEIGHT - SPAWN_AREA_PADDING * 2)
        )
    }

    fun spawnPlayers(roomPlayers: Set<main.kotlin.room.Player>){
        roomPlayers.forEach { roomPlayer ->
            val spawningPlayer = Player(roomPlayer.id, roomPlayer.name, 0.0, 0.0, game)

            determineStartPosition(spawningPlayer)

            game.players.add(spawningPlayer)
        }
    }

    private fun determineStartPosition(spawningPlayer: Player){
        val maxAttempts = 20
        var attempts = 0

        do {
            val spawnPosition = generateRandomSpawnPosition()
            spawningPlayer.xPosition = spawnPosition.x
            spawningPlayer.yPosition = spawnPosition.y
            attempts++
        } while (spawnZonesAreOverlapping(spawningPlayer) && attempts <= maxAttempts)
    }

    private fun generateRandomSpawnPosition(): Vec2D {
        val xPos = Random.nextDouble(SPAWN_AREA.topLeftCorner().x, SPAWN_AREA.topRightCorner().x)
        val yPos = Random.nextDouble(SPAWN_AREA.topLeftCorner().y, SPAWN_AREA.bottomLeftCorner().y)
        return Vec2D(xPos, yPos)
    }

    private fun spawnZonesAreOverlapping(spawningPlayer: Player): Boolean {
        val overlappingPlayer = game.players.firstOrNull { other ->
            val hitMarker = Collision.rectWithRect(other.spawnZone(), spawningPlayer.spawnZone())
            when(hitMarker){
                Collision.HitMarker.NONE -> false
                else -> true
            }
        }

        return overlappingPlayer != null
    }
}