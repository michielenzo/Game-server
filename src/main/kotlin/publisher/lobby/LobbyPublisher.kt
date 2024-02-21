package main.kotlin.publisher.lobby

import main.kotlin.publisher.IPublisher
import main.kotlin.utilities.DTO

object LobbyPublisher: IPublisher<ILobbySubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyLobbyNews(dto)
        }
    }

}