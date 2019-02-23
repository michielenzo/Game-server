package main.kotlin.game

import main.kotlin.game.gameobject.IPowerUp
import main.kotlin.game.gameobject.MedKit
import main.kotlin.utilities.RandomGenerator

class PowerUpSpawner(val gameState: GameState) {

    private val spawnInterval = 5000
    private var millisSinceLastSpawn: Long = System.currentTimeMillis()


    fun tick(){
        if(timeToSpawn()) spawnPowerUp()
    }

    fun timeToSpawn(): Boolean{
        return if(System.currentTimeMillis() >= millisSinceLastSpawn + spawnInterval){
            millisSinceLastSpawn = System.currentTimeMillis(); true
        }else false
    }

    private fun spawnPowerUp() {
        RandomGenerator.randomInt(System.currentTimeMillis(),
                0, GameState.DIMENSION_WIDTH - IPowerUp.WIDTH).also { randX ->
            RandomGenerator.randomInt(System.currentTimeMillis(),
                    0, GameState.DIMENSION_HEIGHT - IPowerUp.HEIGHT).also { randY ->
                gameState.powerUps.add(MedKit(randX, randY))
            }
        }
    }

}