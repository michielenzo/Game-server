package main.kotlin.newspaper

enum class MessageType(val value: String) {
    MESSAGE_TYPE("messageType"),
    CONNECT_TO_SERVER("connect"),
    SEND_GAME_STATE("sendGameState")
}