package main.kotlin.game

abstract class GameLoop: Thread(){

    private val frameRate = 60
    private val millisPerTick = 1000/frameRate

    private var gameOver = false
    private var isPaused = false

    override fun run() {
        val startTime = System.currentTimeMillis()
        var ticks = 0
        while (!gameOver){
            val delta = (System.currentTimeMillis() - startTime) - (ticks * millisPerTick)
            if(delta >= millisPerTick){
                if(!isPaused)
                    tick()
                ticks++
            }
        }
    }

    fun stopLoop(){
        println("game loop stopped")
        gameOver = true
    }

    fun pauseGame(){
        isPaused = true
    }

    fun continueGame(){
        isPaused = false
    }

    abstract fun tick()

}