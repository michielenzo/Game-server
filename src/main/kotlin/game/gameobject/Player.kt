package main.kotlin.game.gameobject

class Player(val sessionId: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int): GameObject{

    companion object {
        const val WIDTH = 40
        const val HEIGHT = 40
    }

    object InputState {
        @Volatile var wKey = false
        @Volatile var aKey = false
        @Volatile var sKey = false
        @Volatile var dKey = false
    }

    private val speed = 5

    override fun tick() {
        move()
    }

    private fun move(){
        if(InputState.wKey) {yPosition -= speed}
        if(InputState.aKey) {xPosition -= speed}
        if(InputState.sKey) {yPosition += speed}
        if(InputState.dKey) {xPosition += speed}
    }

}

