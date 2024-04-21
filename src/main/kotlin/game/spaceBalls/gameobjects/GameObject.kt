package main.kotlin.game.spaceBalls.gameobjects

import main.kotlin.game.spaceBalls.GameProxy

abstract class GameObject {

    var id: Int = 0

    companion object {
        private var HIGHEST_ID = 0
    }

    init {
        this.id = HIGHEST_ID + 1
        HIGHEST_ID++
    }
    abstract fun tick()
}