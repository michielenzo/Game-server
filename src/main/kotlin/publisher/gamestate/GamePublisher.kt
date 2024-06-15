package main.kotlin.publisher.gamestate

import main.kotlin.game.spaceBalls.gameobjects.Player
import main.kotlin.publisher.IPublisher
import main.kotlin.utilities.DTO

object GamePublisher: IPublisher<IGameSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyGameStateNews(dto)
        }
    }

    fun broadcast(dto: DTO, players: List<Player>) {
        subscribers.forEach {
            it.notifyGameStateNews(dto, players)
        }
    }
}