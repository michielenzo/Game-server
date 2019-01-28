package main.kotlin.network.newspaper

import com.google.gson.JsonObject
import main.kotlin.network.dto.DTO


interface INetworkNewsPaper{
    fun broadcast(dto: DTO)
    fun subscribe(newSubscriber: INetworkNewsPaperSubscriber)
}

class NetworkNewsPaper: INetworkNewsPaper {

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