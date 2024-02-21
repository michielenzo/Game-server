package main.kotlin.publisher.network

import main.kotlin.publisher.IPublisher
import main.kotlin.utilities.DTO

object NetworkPublisher: IPublisher<INetworkSubscriber>() {

    val subscriberQueue = mutableListOf<INetworkSubscriber>()

    override fun broadcast(dto: DTO) {
        subscribers.forEach {
            it.notifyNetworkNews(dto)
        }
        popSubscriberQueue()
    }

    private fun popSubscriberQueue() {
        subscriberQueue.forEach {
            subscribers.add(it)
        }
        subscriberQueue.clear()
    }

}