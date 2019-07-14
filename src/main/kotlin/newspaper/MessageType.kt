package main.kotlin.newspaper

enum class MessageType(val value: String) {
    MESSAGE_TYPE("messageType"),
    CONNECT_TO_SERVER("connect"),
    SEND_SPACE_BALLS_GAME_STATE_TO_CLIENTS("sendSpaceBallsGameStateToClients"),
    SEND_ZOMBIES_GAME_STATE_TO_CLIENTS("sendZombiesGameStateToClients"),
    SEND_LOBBY_STATE_TO_CLIENTS("sendLobbyStateToClients"),
    START_GAME_TO_SERVER("startGameToServer"),
    SEND_INPUT_STATE_TO_SERVER("sendInputStateToServer"),
    STOP_GAME_LOOP("stopGameLoop"),
    PAUSE_GAME_LOOP("pauseGameLoop"),
    CONTINUE_GAME_LOOP("continueGameLoop"),
    CHOOSE_NAME_TO_SERVER("chooseNameToServer"),
    BACK_TO_LOBBY_TO_SERVER("backToLobbyToServer"),
    BACK_TO_LOBBY_TO_CLIENT("backToLobbyToServer"),
    CHOOSE_GAMEMODE_TO_SERVER("chooseGameModeToServer");

}