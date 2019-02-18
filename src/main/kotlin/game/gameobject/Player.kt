package main.kotlin.game.gameobject

class Player(val sessionId: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int): GameObject{

    companion object {
        const val WIDTH = 50
        const val HEIGHT = 50
    }

    @Volatile var wKey = false
    @Volatile var aKey = false
    @Volatile var sKey = false
    @Volatile var dKey = false

    private val speed = 2

    override fun tick() {
        move()
    }

    private fun move(){
        if(wKey) {yPosition -= speed}
        if(aKey) {xPosition -= speed}
        if(sKey) {yPosition += speed}
        if(dKey) {xPosition += speed}
    }

}

