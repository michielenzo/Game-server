package main.kotlin.game

import main.kotlin.game.gameobject.IPowerUp
import main.kotlin.game.gameobject.MedKit
import main.kotlin.game.gameobject.Shield
import main.kotlin.utilities.RandomGenerator

class PowerUpSpawner(private val gameState: GameState) {

    private val spawnInterval = 5000
    private var millisSinceLastSpawn: Long = System.currentTimeMillis()

    private val dropTable: List<IPowerUp.PowerUpType> = listOf(
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
            IPowerUp.PowerUpType.MED_KIT,
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
                0, GameState.DIMENSION_WIDTH - IPowerUp.WIDTH).also { randX ->
            RandomGenerator.randomInt(System.currentTimeMillis(),
                    0, GameState.DIMENSION_HEIGHT - IPowerUp.HEIGHT).also { randY ->
                RandomGenerator.randomInt(System.currentTimeMillis(),0, dropTable.size).also { index ->
                    println(index)
                    when(dropTable[index]){
                        IPowerUp.PowerUpType.MED_KIT -> gameState.powerUps.add(MedKit(randX, randY))
                        IPowerUp.PowerUpType.SHIELD -> gameState.powerUps.add(Shield(randX, randY))
                    }
                }
            }
        }
    }

}