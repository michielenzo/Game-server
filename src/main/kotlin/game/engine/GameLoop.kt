package main.kotlin.game.engine

import main.kotlin.game.spaceBalls.dto.ServerTickRateChangedToClientDTO
import main.kotlin.game.spaceBalls.gameobjects.Player
import main.kotlin.game.spaceBalls.gameobjects.toDTO
import main.kotlin.publisher.gamestate.GamePublisher
import java.util.concurrent.CopyOnWriteArrayList

abstract class GameLoop: Thread(){

    val gameEvents = CopyOnWriteArrayList<GameEvent>()

    private var framerate = INITIAL_FRAMERATE
    private var millisPerTick = calculateMillisPerTick()
    open var speedFactor = calculateSpeedFactor()

    companion object {
        private const val INITIAL_FRAMERATE = 60.0
        private const val MILLIS_PER_SECOND = 1000.0
    }

    var gameOver = false
    private var isPaused = false

    private var referenceTime: Long? = null
    private var ticks: Int = 0

    override fun run() {
        referenceTime = System.currentTimeMillis()

        while (!gameOver){
            val delta = (System.currentTimeMillis() - referenceTime!!) - (ticks * millisPerTick)
            if(delta >= millisPerTick){
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

    abstract fun getPlayers(): List<Player>

    fun setTickRate(tickRate: Double) {
        framerate = tickRate
        millisPerTick = calculateMillisPerTick()
        speedFactor = calculateSpeedFactor()
        ticks = 0
        referenceTime = System.currentTimeMillis()
        ServerTickRateChangedToClientDTO(tickRate).also {
            GamePublisher.broadcast(it, getPlayers().map { p -> p.toDTO() })
        }
    }

    fun fireEvent(type: GameEventType) = gameEvents.add(GameEvent(type))

    fun fireEvent(type: GameEventType, data: HashMap<String, String>) =
        gameEvents.add(GameEvent(type).apply { this.data = data })

    private fun calculateMillisPerTick(): Double = MILLIS_PER_SECOND / framerate

    private fun calculateSpeedFactor(): Double = (MILLIS_PER_SECOND / framerate) / MILLIS_PER_SECOND
}