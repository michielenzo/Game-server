package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.gameobjects.powerups.*
import kotlin.random.Random

class PowerUpSpawner(private val game: SpaceBalls) {

    private val spawnInterval = 5000
    private var millisSinceLastSpawn: Long = System.currentTimeMillis()

    private val dropTable: List<IPowerUp.PowerUpType> = listOf(
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.INVERTER,
            IPowerUp.PowerUpType.SHIELD,
            IPowerUp.PowerUpType.CONTROL_INVERTER
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
        Random.nextInt(0, SpaceBalls.DIMENSION_WIDTH - IPowerUp.WIDTH).also { randX ->
            Random.nextInt(0, SpaceBalls.DIMENSION_HEIGHT - IPowerUp.HEIGHT).also { randY ->
                Random.nextInt(0, dropTable.size).also { index ->
                    when(dropTable[index]){
                        IPowerUp.PowerUpType.MED_KIT -> game.powerUps.add(MedKit(game, randX.toDouble(), randY.toDouble()))
                        IPowerUp.PowerUpType.SHIELD -> game.powerUps.add(Shield(game, randX.toDouble(), randY.toDouble()))
                        IPowerUp.PowerUpType.INVERTER -> game.powerUps.add(Inverter(game, randX.toDouble(), randY.toDouble()))
                        IPowerUp.PowerUpType.CONTROL_INVERTER ->
                            game.powerUps.add(ControlInverter(game, randX.toDouble(), randY.toDouble()))
                    }
                }
            }
        }
    }
}