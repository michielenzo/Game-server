package main.kotlin.publisher.gamestate

import main.kotlin.game.spaceBalls.dto.PlayerDTO
import main.kotlin.publisher.IPublisher
import main.kotlin.utilities.DTO

object GamePublisher: IPublisher<IGameSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyGameStateNews(dto)
        }
    }

    fun broadcast(dto: DTO, players: List<PlayerDTO>) {
        subscribers.forEach {
            it.notifyGameStateNews(dto, players)
        }
    }
}