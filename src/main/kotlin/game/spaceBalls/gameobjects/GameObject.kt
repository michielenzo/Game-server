package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.engine.Rectangle

abstract class GameObject {

    var id: Int = 0
    abstract var xPos: Double
    abstract var yPos: Double

    companion object {
        private var HIGHEST_ID = 0
    }

    init {
        this.id = HIGHEST_ID + 1
        HIGHEST_ID++
    }
    abstract fun tick()

    abstract fun spawnZone(): Rectangle
}