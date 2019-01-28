package main.kotlin.network

interface INetworkNewsPaperSubscriber {
    fun notifyNetworkNews(message: String)
}