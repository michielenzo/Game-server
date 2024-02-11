package main.kotlin

import main.kotlin.lobby.Lobby
import network.PlayerWebsocket

fun main(args: Array<String>){
    println("Running on Java version: ${System.getProperty("java.version")}")

    PlayerWebsocket().also {
        it.initialize()
    }
    val lobby = Lobby()
}