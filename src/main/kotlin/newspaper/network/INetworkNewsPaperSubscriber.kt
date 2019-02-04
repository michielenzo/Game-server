package main.kotlin.newspaper.network

import main.kotlin.newspaper.ISubscriber
import main.kotlin.utilities.DTO

interface INetworkNewsPaperSubscriber: ISubscriber {
    fun notifyNetworkNews(dto: DTO)
}