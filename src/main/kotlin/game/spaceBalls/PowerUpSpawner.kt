package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.*
import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import kotlin.random.Random

class PowerUpSpawner(private val game: SpaceBalls) {

    private val spawnInterval = 5000
    private var millisSinceLastSpawn: Long = System.currentTimeMillis()

    private val dropTable: List<PowerUp.Type> = listOf(
            PowerUp.Type.MED_KIT,
            PowerUp.Type.MED_KIT,
            PowerUp.Type.MED_KIT,
            PowerUp.Type.INVERTER,
            PowerUp.Type.SHIELD,
            PowerUp.Type.CONTROL_INVERTER
    )

    fun tick(){
        if(timeToSpawn()) { spawnPowerUp() }
    }

    private fun timeToSpawn(): Boolean{
        return if(System.currentTimeMillis() >= millisSinceLastSpawn + spawnInterval){
            millisSinceLastSpawn = System.currentTimeMillis(); true
        } else false
    }

    private fun spawnPowerUp() {
        val roll = Random.nextInt(0, dropTable.size)
        val powerUp: PowerUp = when(dropTable[roll]) {
            PowerUp.Type.MED_KIT -> MedKit(game, 0.0, 0.0)
            PowerUp.Type.SHIELD -> Shield(game, 0.0, 0.0)
            PowerUp.Type.INVERTER -> Inverter(game, 0.0, 0.0)
            PowerUp.Type.CONTROL_INVERTER -> ControlInverter(game, 0.0, 0.0)
        }

        determineStartPosition(powerUp)

        game.powerUps.add(powerUp)
    }

    private fun determineStartPosition(powerUp: PowerUp){
        val maxAttempts = 20
        var attempts = 0

        do {
            val spawnPosition = generateRandomSpawnPosition()
            powerUp.xPos = spawnPosition.x
            powerUp.yPos = spawnPosition.y
            attempts++
        } while (spawnZonesAreOverlappingWithPlayers(powerUp) && attempts <= maxAttempts)
    }

    private fun generateRandomSpawnPosition(): Vec2D {
        val xPos = Random.nextDouble(0.0, (SpaceBalls.DIMENSION_WIDTH - PowerUp.WIDTH))
        val yPos = Random.nextDouble(0.0, (SpaceBalls.DIMENSION_HEIGHT - PowerUp.HEIGHT))
        return Vec2D(xPos, yPos)
    }

    private fun spawnZonesAreOverlappingWithPlayers(powerUp: PowerUp): Boolean {
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