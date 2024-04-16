package main.kotlin.game.spaceBalls

import main.kotlin.game.engine.RandomGenerator
import main.kotlin.game.spaceBalls.gameobjects.powerups.*

class PowerUpSpawner(private val spaceBalls: SpaceBalls) {

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
        if(timeToSpawn()) spawnPowerUp()
    }

    private fun timeToSpawn(): Boolean{
        return if(System.currentTimeMillis() >= millisSinceLastSpawn + spawnInterval){
            millisSinceLastSpawn = System.currentTimeMillis(); true
        }else false
    }

    private fun spawnPowerUp() {
        RandomGenerator.randomInt(System.currentTimeMillis(),
                0, SpaceBalls.DIMENSION_WIDTH - IPowerUp.WIDTH).also { randX ->
            RandomGenerator.randomInt(System.currentTimeMillis(),
                    0, SpaceBalls.DIMENSION_HEIGHT - IPowerUp.HEIGHT).also { randY ->
                RandomGenerator.randomInt(System.currentTimeMillis(),0, dropTable.size).also { index ->
                    println(index)
                    when(dropTable[index]){
                        IPowerUp.PowerUpType.MED_KIT -> spaceBalls.powerUps.add(MedKit(randX.toDouble(), randY.toDouble()))
                        IPowerUp.PowerUpType.SHIELD -> spaceBalls.powerUps.add(Shield(randX.toDouble(), randY.toDouble()))
                        IPowerUp.PowerUpType.INVERTER -> spaceBalls.powerUps.add(Inverter(randX.toDouble(), randY.toDouble()))
                        IPowerUp.PowerUpType.CONTROL_INVERTER ->
                            spaceBalls.powerUps.add(ControlInverter(randX.toDouble(), randY.toDouble(), spaceBalls))
                    }
                }
            }
        }
    }
}