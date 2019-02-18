package main.kotlin.console.dto

import main.kotlin.newspaper.MessageType
import main.kotlin.utilities.DTO

data class StopGameLoopDTO(val messageType: String = MessageType.STOP_GAME_LOOP.value): DTO()

data class PauseGameLoopDTO(val messageType: String = MessageType.PAUSE_GAME_LOOP.value): DTO()

data class ContinueGameLoopDTO(val messageType: String = MessageType.CONTINUE_GAME_LOOP.value): DTO()