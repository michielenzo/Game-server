package main.kotlin.newspaper.network

import main.kotlin.newspaper.INewsPaper
import main.kotlin.utilities.DTO

object NetworkNewsPaper: INewsPaper<INetworkNewsPaperSubscriber>() {

    val subscriberQueue = mutableListOf<INetworkNewsPaperSubscriber>()

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