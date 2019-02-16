package main.kotlin.game

class Player(val sessionId: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int){

    companion object {
        val WIDTH = 40
        val HEIGHT = 40
    }

}