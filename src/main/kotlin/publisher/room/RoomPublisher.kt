package main.kotlin.publisher.room

import main.kotlin.publisher.IPublisher
import main.kotlin.utilities.DTO

object RoomPublisher: IPublisher<IRoomSubscriber>() {

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyRoomNews(dto)
        }
    }

}