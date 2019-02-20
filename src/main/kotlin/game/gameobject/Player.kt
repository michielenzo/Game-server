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
    var health = 3
    var isAlive = true

    override fun tick() {
        move()
        checkHealth()
    }

    private fun move(){
        if(wKey) {yPosition -= speed}
        if(aKey) {xPosition -= speed}
        if(sKey) {yPosition += speed}
        if(dKey) {xPosition += speed}
    }

    private fun checkHealth() {
        if(health <= 0){
            health = 0
            isAlive = false
        }
    }

}

