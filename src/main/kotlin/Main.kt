package main.kotlin

import main.kotlin.lobby.Lobby
import network.PlayerWebsocket

fun main(args: Array<String>){
    PlayerWebsocket().also {
        it.initialize()
    }
    val lobby = Lobby()
}