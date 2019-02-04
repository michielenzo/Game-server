package main.kotlin.game

import main.kotlin.utilities.DTO

data class PlayerDTO(val sessionId: Int, @Volatile var xPosition: Int, @Volatile var yPosition: Int): DTO()

class Player(val sessionId: String, @Volatile var xPosition: Int, @Volatile var yPosition: Int)