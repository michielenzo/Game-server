package main.kotlin.game.spaceBalls

import main.kotlin.game.spaceBalls.gameobjects.powerups.IPowerUp
import main.kotlin.game.spaceBalls.gameobjects.powerups.Inverter
import main.kotlin.game.spaceBalls.gameobjects.powerups.MedKit
import main.kotlin.game.spaceBalls.gameobjects.powerups.Shield
import main.kotlin.game.engine.RandomGenerator

class PowerUpSpawner(private val spaceBalls: SpaceBalls) {

    private val spawnInterval = 5000
    private var millisSinceLastSpawn: Long = System.currentTimeMillis()

    private val dropTable: List<IPowerUp.PowerUpType> = listOf(
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.INVERTER,
            IPowerUp.PowerUpType.SHIELD
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
                        IPowerUp.PowerUpType.MED_KIT -> spaceBalls.powerUps.add(MedKit(randX, randY))
                        IPowerUp.PowerUpType.SHIELD -> spaceBalls.powerUps.add(Shield(randX, randY))
                        IPowerUp.PowerUpType.INVERTER -> spaceBalls.powerUps.add(Inverter(randX, randY))
                    }
                }
            }
        }
    }

}