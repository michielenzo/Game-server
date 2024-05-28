package main.kotlin

import main.kotlin.room.RoomManager
import network.PlayerWebsocket

fun main(args: Array<String>){
    println("Running on Java version: ${System.getProperty("java.version")}")

    PlayerWebsocket().also {
        it.initialize()
    }
    val roomManager = RoomManager()
}