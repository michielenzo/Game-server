package main.kotlin.game

class Player(val sessionId: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int)