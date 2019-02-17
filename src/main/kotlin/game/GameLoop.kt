package main.kotlin.game

abstract class GameLoop: Thread(){

    private val frameRate = 60
    private val millisPerTick = 1000/frameRate

    var gameOver = false

    override fun run() {
        val startTime = System.currentTimeMillis()
        var ticks = 0
        while (!gameOver){
            val delta = (System.currentTimeMillis() - startTime) - (ticks * millisPerTick)
            if(delta >= millisPerTick){
                tick()
                ticks++
            }
        }
    }

    abstract fun tick()

}