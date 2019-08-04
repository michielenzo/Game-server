package main.kotlin.game.zombies.gameObjects

class Player(val sessionId: String, val name: String, @Volatile var xPos: Int, @Volatile var yPos: Int){

    @Volatile var wKey = false
    @Volatile var aKey = false
    @Volatile var sKey = false
    @Volatile var dKey = false

    var speed = 5

    fun tick(){
        move()
    }

    fun move(){
        if(wKey) yPos -= speed
        if(aKey) xPos -= speed
        if(sKey) yPos += speed
        if(dKey) xPos += speed
    }
}