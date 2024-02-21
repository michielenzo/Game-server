package main.kotlin.publisher.gamestate

import main.kotlin.publisher.IPublisher
import main.kotlin.utilities.DTO

object GameStatePublisher: IPublisher<IGameStateSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyGameStateNews(dto)
        }
    }

}