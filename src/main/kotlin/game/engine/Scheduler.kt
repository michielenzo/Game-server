package main.kotlin.game.engine

import java.util.Timer
import java.util.TimerTask

object Scheduler {
    fun schedule(timeInMillis: Long, callback: () -> Unit) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                callback()
            }
        }, timeInMillis)
    }
}