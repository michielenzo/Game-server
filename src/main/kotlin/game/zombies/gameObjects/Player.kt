package main.kotlin.game.zombies.gameObjects

class Player(val sessionId: String, val name: String, @Volatile var xPos: Int, @Volatile var yPos: Int)