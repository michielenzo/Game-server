package main.kotlin.publisher.gamestate

import main.kotlin.game.spaceBalls.dto.PlayerDTO
import main.kotlin.publisher.ISubscriber
import main.kotlin.utilities.DTO

interface IGameSubscriber: ISubscriber {
    fun notifyGameStateNews(dto: DTO)

    fun notifyGameStateNews(dto: DTO, players: List<PlayerDTO>)
}
