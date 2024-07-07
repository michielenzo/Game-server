package main.kotlin.game.engine

class Rectangle(var x: Double, var y: Double, var width: Double, var height: Double)

fun Rectangle.topLeftCorner(): Vec2D = Vec2D(x, y)
fun Rectangle.bottomLeftCorner(): Vec2D = Vec2D(x, y + height)
fun Rectangle.topRightCorner(): Vec2D = Vec2D(x + width, y)
fun Rectangle.bottomRightCorner(): Vec2D = Vec2D(x + width, y + height)
