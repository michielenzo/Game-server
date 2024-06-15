package main.kotlin.publisher

enum class MsgType(val value: String) {
    MESSAGE_TYPE("messageType"),
    CONNECT_TO_SERVER("connect"),
    SEND_SPACE_BALLS_GAME_STATE_TO_CLIENTS("sendSpaceBallsGameStateToClients"),
    SEND_ZOMBIES_GAME_STATE_TO_CLIENTS("sendZombiesGameStateToClients"),
    SEND_ROOM_STATE_TO_CLIENTS("sendRoomStateToClients"),
    START_GAME_TO_SERVER("startGameToServer"),
    SEND_INPUT_STATE_TO_SERVER("sendInputStateToServer"),
    STOP_GAME_LOOP("stopGameLoop"),
    PAUSE_GAME_LOOP("pauseGameLoop"),
    CONTINUE_GAME_LOOP("continueGameLoop"),
    CHOOSE_NAME_TO_SERVER("chooseNameToServer"),
    BACK_TO_ROOM_TO_SERVER("backToRoomToServer"),
    BACK_TO_ROOM_TO_CLIENT("backToRoomToClient"),
    CHOOSE_GAMEMODE_TO_SERVER("chooseGameModeToServer"),
    HEARTBEAT_ACKNOWLEDGE("heartbeatAcknowledge"),
    HEARTBEAT_CHECK("heartbeatCheck"),
    JOIN_ROOM_TO_SERVER("joinRoomToServer"),
    ROOM_NOT_FOUND_TO_CLIENT("roomNotFoundToClient"),
    KICK_PLAYER_TO_SERVER("kickPlayerToServer"),
    PROMOTE_PLAYER_TO_SERVER("promotePlayerToServer"),
    YOU_HAVE_BEEN_KICKED_TO_CLIENT("youHaveBeenKickedToClient"),
    READY_UP_TO_SERVER("readyUpToServer"),
    NOT_READY_TO_SERVER("notReadyToServer"),
    GAME_CONFIG_TO_CLIENTS("gameConfigToClients");
}