package main.kotlin.publisher.network

import main.kotlin.publisher.ISubscriber
import main.kotlin.utilities.DTO

interface INetworkSubscriber: ISubscriber {
    fun notifyNetworkNews(dto: DTO)
}