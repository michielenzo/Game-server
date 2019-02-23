package main.kotlin.utilities

import java.util.*

object RandomGenerator{

    fun randomInt(seed: Long, min: Int, max: Int): Int{
        val range = max - min
        var num: Int? = null
        Random(seed).nextInt().also { init ->
            num = if(init < 0) -init
            else init
        }
        return (num!! %range)+min
    }

}