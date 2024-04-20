package main.kotlin.game.engine

abstract class GameLoop: Thread(){
    companion object {
        private const val FRAMERATE = 60
        private const val MILLIS_PER_SECOND = 1000.0
        private const val MILLIS_PER_TICK = MILLIS_PER_SECOND / FRAMERATE

        const val SPEED_FACTOR = (MILLIS_PER_SECOND / FRAMERATE) / MILLIS_PER_SECOND
    }

    var gameOver = false
    private var isPaused = false

    override fun run() {
        val startTime = System.currentTimeMillis()
        var ticks = 0
        while (!gameOver){
            val delta = (System.currentTimeMillis() - startTime) - (ticks * MILLIS_PER_TICK)
            if(delta >= MILLIS_PER_TICK){
                if(!isPaused)
                    tick()
                ticks++
            }
        }
    }

    fun stopLoop(){
        gameOver = true
    }

    fun pauseGame(){
        isPaused = true
    }

    fun continueGame(){
        isPaused = false
    }

    open fun startGame(){
        start()
    }

    abstract fun tick()
}