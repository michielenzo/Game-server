package main.kotlin.newspaper.network

import main.kotlin.newspaper.INewsPaper
import main.kotlin.utilities.DTO

class NetworkNewsPaper: INewsPaper<INetworkNewsPaperSubscriber> {

    private val subscribers: MutableList<INetworkNewsPaperSubscriber> = mutableListOf()

    companion object {
        private val networkNewsPaper = NetworkNewsPaper()

        fun getInstance(): NetworkNewsPaper {
            return networkNewsPaper
        }
    }

    override fun subscribe(newSubscriber: INetworkNewsPaperSubscriber) {
        subscribers.add(newSubscriber)
    }

    override fun broadcast(dto: DTO) {
        subscribers.forEach { subscriber ->
            subscriber.notifyNetworkNews(dto)
        }
    }

}