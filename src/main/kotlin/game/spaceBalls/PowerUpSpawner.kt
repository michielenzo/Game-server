package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import kotlin.random.Random

class PowerUpSpawner(private val game: SpaceBalls) {

    private val spawnInterval = 5000
    private var millisSinceLastSpawn: Long = System.currentTimeMillis()

    private val dropTable: List<PowerUp.PowerUpType> = listOf(
            PowerUp.PowerUpType.MED_KIT,
            PowerUp.PowerUpType.MED_KIT,
            PowerUp.PowerUpType.MED_KIT,
            PowerUp.PowerUpType.INVERTER,
            PowerUp.PowerUpType.SHIELD,
            PowerUp.PowerUpType.CONTROL_INVERTER
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
        Random.nextInt(0, (SpaceBalls.DIMENSION_WIDTH - PowerUp.WIDTH).toInt()).also { randX ->
            Random.nextInt(0, (SpaceBalls.DIMENSION_HEIGHT - PowerUp.HEIGHT).toInt()).also { randY ->
                Random.nextInt(0, dropTable.size).also { index ->
                    when(dropTable[index]){
                        PowerUp.PowerUpType.MED_KIT -> game.powerUps.add(MedKit(game, randX.toDouble(), randY.toDouble()))
                        PowerUp.PowerUpType.SHIELD -> game.powerUps.add(Shield(game, randX.toDouble(), randY.toDouble()))
                        PowerUp.PowerUpType.INVERTER -> game.powerUps.add(Inverter(game, randX.toDouble(), randY.toDouble()))
                        PowerUp.PowerUpType.CONTROL_INVERTER ->
                            game.powerUps.add(ControlInverter(game, randX.toDouble(), randY.toDouble()))
                    }
                }
            }
        }
    }
}