package main.kotlin.newspaper.network

import main.kotlin.newspaper.INewsPaper
import main.kotlin.utilities.DTO

object NetworkNewsPaper: INewsPaper<INetworkNewsPaperSubscriber> {

    private val subscribers: MutableList<INetworkNewsPaperSubscriber> = mutableListOf()

    override fun subscribe(newSubscriber: INetworkNewsPaperSubscriber) {
        subscribers.add(newSubscriber)
    }

    override fun broadcast(dto: DTO) {
        subscribers.forEach { subscriber ->
            println("hello")
            subscriber.notifyNetworkNews(dto)
        }
    }

}