package main.kotlin.game.engine

import java.awt.Point
import kotlin.math.sqrt

object Collision {

    /*
    * circle seen from its center and rectangle seen from its top-left corner
    * */

    fun rectWithCircle(rect: Rectangle, circle: Circle): HitMarker {
        val rad = circle.radius
        val distanceTopLeft = distance(Point(rect.x.toInt(), rect.y.toInt()),
                Point(circle.centerX.toInt(), circle.centerY.toInt()))
        val distanceTopRight = distance(Point(rect.x.toInt() + rect.width.toInt(), rect.y.toInt()),
                Point(circle.centerX.toInt(), circle.centerY.toInt()))
        val distanceBottomLeft = distance(Point(rect.x.toInt(), rect.y.toInt() + rect.height.toInt()),
                Point(circle.centerX.toInt(), circle.centerY.toInt()))
        val distanceBottomRight = distance(Point(rect.x.toInt() + rect.width.toInt(), rect.y.toInt() + rect.height.toInt()),
                Point(circle.centerX.toInt(), circle.centerY.toInt()))

        if(distanceTopLeft <= rad && circle.centerX <= rect.x && circle.centerY <= rect.y) {
            return HitMarker.TOP_LEFT_CORNER
        }else if(distanceTopRight <= rad && circle.centerX >= rect.x + rect.width && circle.centerY <= rect.y) {
            return HitMarker.TOP_RIGHT_CORNER
        }else if(distanceBottomLeft <= rad && circle.centerX <= rect.x && circle.centerY >= rect.y) {
            return HitMarker.BOTTOM_LEFT_CORNER
        }else if(distanceBottomRight <= rad && circle.centerX >= rect.x + rect.width && circle.centerY >= rect.y) {
            return HitMarker.BOTTOM_RIGHT_CORNER
        }else if(circle.centerX >= rect.x && circle.centerX <= rect.x + rect.width && circle.centerY >= rect.y && circle.centerY <= rect.y + rect.height){
            return HitMarker.INSIDE
        }else if(circle.centerY >= rect.y && circle.centerY <= rect.y + rect.height){
            if(circle.centerX + rad >= rect.x && circle.centerX - rad <= rect.x + rect.width){
                return if(circle.centerX > rect.x + rect.width){
                    HitMarker.RIGHT_WALL
                }else{
                    HitMarker.LEFT_WALL
                }
            }
        }else if(circle.centerX >= rect.x && circle.centerX <= rect.x + rect.width){
            if(circle.centerY + rad >= rect.y && circle.centerY - rad <= rect.y + rect.height){
                return if(circle.centerY < rect.y){
                    HitMarker.ROOF
                }else{
                    HitMarker.FLOOR
                }
            }
        }
        return HitMarker.NONE
    }

    fun rectWithRect(rectA: Rectangle, rectB: Rectangle): HitMarker {
        if(rectA.x + rectA.width >= rectB.x && rectA.x <= rectB.x + rectB.width &&
           rectA.y + rectA.height >= rectB.y && rectA.y <= rectB.y + rectB.height)
        {
            return HitMarker.SOMEWHERE
        }
        return HitMarker.NONE
    }

    private fun distance(a: Point, b: Point): Double{
        var o = a.x - b.x
        var a = a.y - b.y
        if(o < 0) o = -o
        if(a < 0) a = -a
        return sqrt((o*o + a*a).toDouble())
    }

    enum class HitMarker{
        TOP_LEFT_CORNER,
        TOP_RIGHT_CORNER,
        BOTTOM_LEFT_CORNER,
        BOTTOM_RIGHT_CORNER,
        ROOF,
        FLOOR,
        LEFT_WALL,
        RIGHT_WALL,
        INSIDE,
        SOMEWHERE,
        NONE
    }

}