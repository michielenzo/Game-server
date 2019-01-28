package main.kotlin.network


interface INetworkNewsPaper{
    fun broadcast(message: String)
    fun subscribe(newSubscriber: INetworkNewsPaperSubscriber)
}

class NetworkNewsPaper: INetworkNewsPaper{

    private val subscribers: MutableList<INetworkNewsPaperSubscriber> = mutableListOf()

    companion object {
        private val networkNewsPaper = NetworkNewsPaper()

        fun getInstance(): NetworkNewsPaper{
            return networkNewsPaper
        }
    }

    override fun subscribe(newSubscriber: INetworkNewsPaperSubscriber) {
        subscribers.add(newSubscriber)
    }

    override fun broadcast(message: String) {
        subscribers.forEach { subscriber ->
            subscriber.notifyNetworkNews(message)
        }
    }

}